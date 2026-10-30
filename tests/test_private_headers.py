"""Synthetic checks for common PEM secret headers in public tracked files."""
import tempfile
import unittest
from pathlib import Path

from tools.audit_tracked import audit


class PrivateHeaderVariantsTest(unittest.TestCase):
    @staticmethod
    def marker(kind=""):
        prefix = (kind + " ") if kind else ""
        return "-----BEGIN " + prefix + "PRI" + "VATE KEY-----"

    def test_known_key_variants_are_rejected(self):
        for kind in ("", "RSA", "EC", "OPENSSH", "DSA", "ENCRYPTED"):
            with self.subTest(kind=kind), tempfile.TemporaryDirectory() as directory:
                root = Path(directory)
                path = root / "example.txt"
                path.write_text(self.marker(kind) + "\nfixture not a real credential\n")
                issues = audit(root, [path.name])
                self.assertEqual([{"path": path.name, "reason": "embedded_private_key"}], issues)

    def test_safe_public_and_certificate_markers_are_allowed(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            data = {
                "certificate.txt": "-----BEGIN CERTIFICATE-----\nfixture\n",
                "public.txt": "-----BEGIN PUBLIC KEY-----\nfixture\n",
                "reference.txt": "The words private key are not themselves a credential.",
                "readme.md": "ordinary public documentation",
            }
            for name, content in data.items():
                (root / name).write_text(content)
            self.assertEqual([], audit(root, list(data)))

    def test_forbidden_filenames_still_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / "local.properties").write_text("not a real credential")
            issues = audit(root, ["local.properties"])
            self.assertEqual("forbidden_public_artifact", issues[0]["reason"])


if __name__ == "__main__":
    unittest.main()
