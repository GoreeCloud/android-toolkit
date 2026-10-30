"""Synthetic archive fixtures verify SDK packaging assertions without Android SDK."""
import io
import tempfile
import unittest
import zipfile
from pathlib import Path

from tools.check_binaries import ANDROID, CORE, verify


def archive_bytes(names):
    buffer = io.BytesIO()
    with zipfile.ZipFile(buffer, "w") as archive:
        for name in names:
            archive.writestr(name, b"fixture")
    return buffer.getvalue()


class ArtifactVerificationTests(unittest.TestCase):
    def write_pair(self, directory, core_members=CORE, android_members=ANDROID):
        root = Path(directory)
        core = root / "sdk-core.jar"
        aar = root / "android-kit.aar"
        core.write_bytes(archive_bytes(sorted(core_members)))
        with zipfile.ZipFile(aar, "w") as archive:
            archive.writestr("AndroidManifest.xml", b"fixture")
            archive.writestr("classes.jar", archive_bytes(sorted(android_members)))
        return core, aar

    def test_accepts_expected_structures_without_claiming_binary_compatibility(self):
        with tempfile.TemporaryDirectory() as root:
            core, aar = self.write_pair(root)
            result = verify(core, aar)
            self.assertTrue(result["ok"])
            self.assertFalse(result["binary_compatibility_verified"])
            self.assertEqual(64, len(result["core_jar_sha256"]))

    def test_rejects_missing_core_public_api(self):
        with tempfile.TemporaryDirectory() as root:
            core, aar = self.write_pair(root, core_members=set(CORE) - {next(iter(CORE))})
            with self.assertRaisesRegex(ValueError, "missing expected members"):
                verify(core, aar)

    def test_rejects_missing_android_public_api(self):
        with tempfile.TemporaryDirectory() as root:
            core, aar = self.write_pair(root, android_members=set(ANDROID) - {next(iter(ANDROID))})
            with self.assertRaisesRegex(ValueError, "missing expected members"):
                verify(core, aar)

    def test_rejects_unreadable_missing_artifact(self):
        with tempfile.TemporaryDirectory() as root:
            core, aar = self.write_pair(root)
            aar.unlink()
            with self.assertRaisesRegex(ValueError, "Missing artifact"):
                verify(core, aar)


if __name__ == "__main__":
    unittest.main()
