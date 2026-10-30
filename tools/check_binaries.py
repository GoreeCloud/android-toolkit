#!/usr/bin/env python3
"""Offline verification of SDK JAR and Android AAR public classes and integrity."""
from __future__ import annotations

import argparse
import hashlib
import io
import json
import zipfile
from pathlib import Path

MAX_ARTIFACT_BYTES = 100 * 1024 * 1024
CORE = {
    "com/goreecloud/android/core/TrustedEndpoint.class",
    "com/goreecloud/android/core/RetryBackoff.class",
    "com/goreecloud/android/core/SafeFileName.class",
    "com/goreecloud/android/core/Sha256.class",
}
ANDROID = {
    "com/goreecloud/android/kit/AndroidSettingsIntents.class",
    "com/goreecloud/android/kit/AppScopedAtomicWriter.class",
}


def _require_members(archive: zipfile.ZipFile, members: set[str], label: str) -> int:
    names = archive.namelist()
    if len(names) != len(set(names)):
        raise ValueError(f"{label}: duplicate ZIP member")
    missing = members - set(names)
    if missing:
        raise ValueError(f"{label}: missing expected members: {sorted(missing)}")
    bad = archive.testzip()
    if bad is not None:
        raise ValueError(f"{label}: ZIP integrity check failed")
    return len(names)


def _read_file(path: Path) -> bytes:
    if not path.is_file():
        raise ValueError(f"Missing artifact: {path.name}")
    size = path.stat().st_size
    if size == 0 or size > MAX_ARTIFACT_BYTES:
        raise ValueError("Artifact is empty or exceeds 100 MiB limit")
    return path.read_bytes()


def verify(core_jar: Path, android_aar: Path) -> dict[str, object]:
    core_bytes = _read_file(core_jar)
    aar_bytes = _read_file(android_aar)
    try:
        with zipfile.ZipFile(io.BytesIO(core_bytes)) as core:
            core_count = _require_members(core, CORE, "sdk-core JAR")
        with zipfile.ZipFile(io.BytesIO(aar_bytes)) as aar:
            aar_count = _require_members(
                aar, {"AndroidManifest.xml", "classes.jar"}, "Android AAR")
            embedded = aar.read("classes.jar")
            if not embedded or len(embedded) > MAX_ARTIFACT_BYTES:
                raise ValueError("Android AAR classes.jar invalid")
        with zipfile.ZipFile(io.BytesIO(embedded)) as android_classes:
            android_count = _require_members(
                android_classes, ANDROID, "Android AAR classes.jar")
    except (OSError, zipfile.BadZipFile) as exc:
        raise ValueError("Invalid SDK artifact ZIP structure") from exc
    return {
        "ok": True,
        "sdk_core_classes_present": len(CORE),
        "android_public_classes_present": len(ANDROID),
        "core_jar_entries": core_count,
        "android_aar_entries": aar_count,
        "android_classes_entries": android_count,
        "core_jar_sha256": hashlib.sha256(core_bytes).hexdigest(),
        "android_aar_sha256": hashlib.sha256(aar_bytes).hexdigest(),
        "binary_compatibility_verified": False,
        "note": "Build artifact contents only; no published SDK or independent consumer ABI guarantee.",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--core", required=True, type=Path)
    parser.add_argument("--aar", required=True, type=Path)
    args = parser.parse_args()
    try:
        report = verify(args.core, args.aar)
    except (ValueError, OSError) as exc:
        print(json.dumps({"ok": False, "reason": str(exc)}))
        return 1
    print(json.dumps(report, sort_keys=True))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
