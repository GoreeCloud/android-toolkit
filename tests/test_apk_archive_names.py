import tempfile
import unittest
import zipfile
from pathlib import Path

from tools.apk_inspect import inspect


class ApkArchiveNameValidationTest(unittest.TestCase):
    def test_rejects_noncanonical_names(self):
        for name in ("nested//file", "nested/./file", "C:/file"):
            with self.subTest(name=name), tempfile.TemporaryDirectory() as directory:
                path = Path(directory) / "invalid.apk"
                with zipfile.ZipFile(path, "w") as archive:
                    archive.writestr("AndroidManifest.xml", "fixture")
                    archive.writestr(name, "fixture")
                with self.assertRaises(ValueError):
                    inspect(path)


if __name__ == "__main__":
    unittest.main()
