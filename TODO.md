# TODO and algorythms info

## DB fields

1. [ ] All in ordered array -- array/table
2. [ ] Timestamp of patch creation -- timestamp
   - 2.1. [&check;] Filename (w/ relative path) -- string
   - 2.2. [&check;] Ordered array of changes -- array/table
     - 2.2.1. [&check;] Mod type (added/removed) -- bool
     - 2.2.2. [&check;] Mod start index (byte pos in file) -- longboi int
     - 2.2.3. [&check;] Mod substring of bytes -- string/bytearray
   - 2.3. [&check;] File checksum -- string/long
3. [ ] Maybe message (commit-alike about version) -- string

## Patcher info (on local)

1. [ ] Current patch id (index in DB array)

## Three steps test patch (all base variants)

1. [ ] File created: (empty) -> (test_old.txt)
2. [&check;] File updated: (test_old.txt) -> (test_new.txt)
3. [ ] File deleted: (test_new.txt) -> (empty)

## Patch basic pipeline

1. [&check;] Check checksum
2. [ ] Check files bitwise
