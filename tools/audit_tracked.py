#!/usr/bin/env python3
"""Fail closed on high-risk tracked files before publishing a public Android repository."""
from __future__ import annotations

import argparse
import json
import re
import subprocess
from pathlib import Path

FORBIDDEN_NAMES = {"local.properties", "google-services.json", ".env", "id_rsa", "id_ed25519"}
FORBIDDEN_SUFFIXES = {".apk", ".aab", ".jks", ".keystore", ".p12", ".pfx", ".pem", ".key"}
PRIVATE_KEY_HEADER = re.compile(rb"-{5}BEGIN (?:[A-Z0-9]+ )*" + b"PRI" + b"VATE KEY-{5}")


def audit(root: Path, paths: list[str]) -> list[dict[str, str]]:
    root = root.resolve(strict=True)
    issues: list[dict[str, str]] = []
    for relative in paths:
        path = root / relative
        # A tracked symlink may intentionally point elsewhere: forbid it in a public toolkit.
        if path.is_symlink():
            issues.append({"path": relative, "reason": "tracked_symlink"})
            continue
        if not path.is_file() or not path.resolve().is_relative_to(root):
            issues.append({"path": relative, "reason": "missing_or_escaping_path"})
            continue
        name = path.name.lower()
        if name in FORBIDDEN_NAMES or name.startswith(".env.") or path.suffix.lower() in FORBIDDEN_SUFFIXES:
            issues.append({"path": relative, "reason": "forbidden_public_artifact"})
            continue
        if path.stat().st_size <= 2_000_000 and PRIVATE_KEY_HEADER.search(path.read_bytes()) is not None:
            issues.append({"path": relative, "reason": "embedded_private_key"})
    return issues


def git_paths(root: Path) -> list[str]:
    proc = subprocess.run(["git", "-C", str(root), "ls-files", "-z"], check=True, capture_output=True)
    return sorted(entry.decode("utf-8") for entry in proc.stdout.split(b"\x00") if entry)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", type=Path, default=Path(__file__).resolve().parents[1])
    args = parser.parse_args()
    try:
        paths = git_paths(args.root)
        issues = audit(args.root, paths)
    except (OSError, subprocess.CalledProcessError) as exc:
        print(json.dumps({"ok": False, "error": str(exc)}))
        return 2
    print(json.dumps({"ok": not issues, "tracked_files": len(paths), "issues": issues}, sort_keys=True))
    return 1 if issues else 0


if __name__ == "__main__":
    raise SystemExit(main())
