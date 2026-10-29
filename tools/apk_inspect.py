#!/usr/bin/env python3
"""Offline, bounded APK ZIP integrity checks; NOT signature or runtime verification."""
from __future__ import annotations

import argparse
import hashlib
import json
import zipfile
from pathlib import Path, PurePosixPath

MAX_ARCHIVE_BYTES = 300 * 1024 * 1024
MAX_UNCOMPRESSED_BYTES = 750 * 1024 * 1024
MAX_ENTRY_COUNT = 30_000


def inspect(path: Path) -> dict[str, object]:
    path = path.resolve(strict=True)
    size = path.stat().st_size
    if not path.is_file() or size == 0 or size > MAX_ARCHIVE_BYTES:
        raise ValueError("APK missing, empty, or above the 300 MiB size limit")
    checksum = hashlib.sha256()
    with path.open("rb") as stream:
        for block in iter(lambda: stream.read(1024 * 1024), b""):
            checksum.update(block)
    with zipfile.ZipFile(path) as archive:
        files = archive.infolist()
        if not 0 < len(files) <= MAX_ENTRY_COUNT:
            raise ValueError("APK entry count outside configured bounds")
        names: set[str] = set()
        total = 0
        for entry in files:
            name = entry.filename
            if (not name or name.startswith("/") or "\\" in name or
                    ".." in PurePosixPath(name).parts or
                    PurePosixPath(name).is_absolute()):
                raise ValueError("Unsafe archive entry path")
            if name in names:
                raise ValueError("Duplicate archive entry")
            names.add(name)
            total += entry.file_size
            if total > MAX_UNCOMPRESSED_BYTES:
                raise ValueError("APK expanded contents exceed allowed bounds")
            if entry.flag_bits & 1:
                raise ValueError("Encrypted APK entry is unsupported")
        if "AndroidManifest.xml" not in names:
            raise ValueError("Missing AndroidManifest.xml")
        # Do not extract untrusted entries: bounded CRC scan is sufficient here.
        bad = archive.testzip()
        if bad is not None:
            raise ValueError("Corrupt APK entry")
    return {
        "ok": True,
        "file": path.name,
        "size_bytes": size,
        "sha256": checksum.hexdigest(),
        "entries": len(files),
        "signature_verified": False,
        "package_id_verified": False,
        "device_tested": False,
        "note": "ZIP checks only; use apksigner and a native-device acceptance procedure separately.",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("apk", type=Path)
    args = parser.parse_args()
    try:
        result = inspect(args.apk)
    except (OSError, ValueError, zipfile.BadZipFile, RuntimeError) as exc:
        print(json.dumps({"ok": False, "reason": str(exc)}))
        return 1
    print(json.dumps(result, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
