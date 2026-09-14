#!/usr/bin/env python3
"""Read APK archive/manifest metadata without executing or decompiling the app.

Standard library only. This is NOT signature verification or a security audit.
"""
import argparse
import hashlib
import json
import struct
import zipfile
from pathlib import Path


def manifest_elements(data):
    def u16(offset):
        return struct.unpack_from("<H", data, offset)[0]

    def u32(offset):
        return struct.unpack_from("<I", data, offset)[0]

    def length8(offset):
        value = data[offset]
        if value & 0x80:
            return ((value & 0x7f) << 8) | data[offset + 1], offset + 2
        return value, offset + 1

    if u16(0) != 0x0003 or u32(4) != len(data):
        raise ValueError("Expected Android binary XML")
    strings, result = [], []
    offset = u16(2)
    while offset < len(data):
        kind, header, size = struct.unpack_from("<HHI", data, offset)
        if size < header or header < 8 or offset + size > len(data):
            raise ValueError("Invalid XML chunk")
        if kind == 0x0001:
            count, flags, start = u32(offset + 8), u32(offset + 16), u32(offset + 20)
            for index in range(count):
                pos = offset + start + u32(offset + header + index * 4)
                if flags & 0x100:
                    _, pos = length8(pos)
                    length, pos = length8(pos)
                    value = data[pos:pos + length].decode("utf-8")
                else:
                    length = u16(pos)
                    pos += 2
                    if length & 0x8000:
                        length = ((length & 0x7fff) << 16) | u16(pos)
                        pos += 2
                    value = data[pos:pos + length * 2].decode("utf-16le")
                strings.append(value)
        elif kind == 0x0102:
            ext = offset + header
            tag = strings[u32(ext + 4)]
            start, step, count = u16(ext + 8), u16(ext + 10), u16(ext + 12)
            attrs = {}
            for index in range(count):
                pos = ext + start + index * step
                name, raw, dtype, value = u32(pos + 4), u32(pos + 8), data[pos + 15], u32(pos + 16)
                if raw != 0xffffffff:
                    decoded = strings[raw]
                elif dtype == 0x03:
                    decoded = strings[value]
                elif dtype == 0x12:
                    decoded = bool(value)
                elif dtype in (0x10, 0x11):
                    decoded = value
                else:
                    decoded = f"@0x{value:08x}"
                attrs[strings[name]] = decoded
            result.append({"tag": tag, "attributes": attrs})
        offset += size
    return result


def inspect(path):
    raw = path.read_bytes()
    with zipfile.ZipFile(path) as archive:
        bad_entry = archive.testzip()
        if bad_entry:
            raise ValueError(f"CRC failure: {bad_entry}")
        elements = manifest_elements(archive.read("AndroidManifest.xml"))
        names = archive.namelist()
        dex = archive.read("classes.dex")
        count, table = struct.unpack_from("<II", dex, 56)
        classes = []
        for index in range(count):
            pos = struct.unpack_from("<I", dex, table + index * 4)[0]
            while dex[pos] & 0x80:
                pos += 1
            pos += 1
            value = dex[pos:dex.index(0, pos)].decode("utf-8", errors="replace")
            if value.startswith("Lapp/nasma/") and value.endswith(";"):
                classes.append(value)
    return {
        "file": path.name,
        "size_bytes": len(raw),
        "sha256": hashlib.sha256(raw).hexdigest(),
        "zip_crc_valid": True,
        "manifest_elements": elements,
        "nasma_dex_class_descriptors": classes,
        "archive_entries": names,
        "limitations": [
            "Static metadata only; the application was not installed or executed.",
            "A signature file in META-INF is not proof that a signature is valid.",
            "Resources referenced by ID are not resolved to labels or values.",
            "DEX class names are not recovered source code or a verified applicationId.",
        ],
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("apk", type=Path)
    parser.add_argument("--expected-sha256", help="Fail unless the preserved APK matches this digest")
    args = parser.parse_args()
    report = inspect(args.apk)
    if args.expected_sha256 and report["sha256"] != args.expected_sha256.lower():
        raise SystemExit("APK SHA-256 does not match the preserved baseline")
    print(json.dumps(report, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
