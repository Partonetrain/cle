import sys

def unique_lines(path):
    seen = set()
    result = []
    with open(path, encoding="utf-8") as f:
        for line in f:
            line = line.rstrip("\n")
            if line.startswith("-") or line in seen:
                continue
            seen.add(line)
            result.append(line)
    return result

if __name__ == "__main__":
    if len(sys.argv) != 2:
        sys.exit(f"Usage: {sys.argv[0]} <file.txt>")

    lines = unique_lines(sys.argv[1])
    print(f"Unique lines: {len(lines)}", file=sys.stderr)
    print("\n".join(lines))