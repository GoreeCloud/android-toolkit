import tempfile
import unittest
import zipfile
from pathlib import Path

from tools.apk_inspect import inspect
from tools.audit_tracked import audit


class ApkInspectorTests(unittest.TestCase):
    def test_valid_synthetic_zip_is_not_marked_signed(self):
        with tempfile.TemporaryDirectory() as d:
            path = Path(d) / "fixture.apk"
            with zipfile.ZipFile(path, "w") as z:
                z.writestr("AndroidManifest.xml", "stub; not an installable manifest")
                z.writestr("classes.dex", "fixture")
            result = inspect(path)
            self.assertTrue(result["ok"])
            self.assertFalse(result["signature_verified"])
            self.assertFalse(result["device_tested"])
            self.assertEqual(64, len(result["sha256"]))

    def test_path_traversal_rejected(self):
        with tempfile.TemporaryDirectory() as d:
            path = Path(d) / "bad.apk"
            with zipfile.ZipFile(path, "w") as z:
                z.writestr("AndroidManifest.xml", "stub")
                z.writestr("../outside", "bad")
            with self.assertRaises(ValueError):
                inspect(path)

    def test_manifest_required(self):
        with tempfile.TemporaryDirectory() as d:
            path = Path(d) / "bad.apk"
            with zipfile.ZipFile(path, "w") as z:
                z.writestr("classes.dex", "fixture")
            with self.assertRaises(ValueError):
                inspect(path)


class TrackedAuditTests(unittest.TestCase):
    def test_public_safe_files(self):
        with tempfile.TemporaryDirectory() as d:
            root = Path(d)
            (root / "README.md").write_text("public")
            self.assertEqual([], audit(root, ["README.md"]))

    def test_sensitive_files_and_symlink(self):
        with tempfile.TemporaryDirectory() as d:
            root = Path(d)
            (root / "google-services.json").write_text("private")
            (root / "secret.pem").write_text("private")
            (root / "link").symlink_to("google-services.json")
            issues = audit(root, ["google-services.json", "secret.pem", "link"])
            self.assertEqual(3, len(issues))

    def test_embedded_private_key_header(self):
        with tempfile.TemporaryDirectory() as d:
            root = Path(d)
            (root / "bad.txt").write_bytes(b"-----BEGIN " + b"PRIVATE KEY-----\nsecret")
            issues = audit(root, ["bad.txt"])
            self.assertEqual("embedded_private_key", issues[0]["reason"])


if __name__ == "__main__":
    unittest.main()
