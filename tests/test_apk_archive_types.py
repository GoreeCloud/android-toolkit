import stat
import tempfile
import unittest
import zipfile
from pathlib import Path

from tools.apk_inspect import inspect


class ApkArchiveEntryTypeTests(unittest.TestCase):
    def _archive(self, entry_name, mode):
        directory = tempfile.TemporaryDirectory()
        path = Path(directory.name) / "fixture.apk"
        special = zipfile.ZipInfo(entry_name)
        special.create_system = 3
        special.external_attr = (mode | 0o755) << 16
        with zipfile.ZipFile(path, "w") as archive:
            archive.writestr("AndroidManifest.xml", "fixture")
            archive.writestr(special, "fixture")
        return directory, path

    def test_symlink_entry_metadata_rejected(self):
        directory, path = self._archive("assets/link", stat.S_IFLNK)
        with directory:
            with self.assertRaisesRegex(ValueError, "special-file"):
                inspect(path)

    def test_noncanonical_directory_metadata_rejected(self):
        directory, path = self._archive("assets", stat.S_IFDIR)
        with directory:
            with self.assertRaisesRegex(ValueError, "Directory entry"):
                inspect(path)

    def test_regular_entry_metadata_allowed(self):
        directory, path = self._archive("assets/file.txt", stat.S_IFREG)
        with directory:
            self.assertTrue(inspect(path)["ok"])


if __name__ == "__main__":
    unittest.main()
