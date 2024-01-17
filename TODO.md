# TODO and algorythms info

## DB fields

1. [ ] All in ordered array -- array/table
2. [ ] Timestamp of patch creation -- timestamp
   - 2.1. [x] Filename (w/ relative path) -- string
   - 2.2. [x] Ordered array of changes -- array/table
     - 2.2.1. [x] Mod type (added/removed) -- bool
     - 2.2.2. [x] Mod start index (byte pos in file) -- longboi int
     - 2.2.3. [x] Mod substring of bytes -- string/bytearray
   - 2.3. [x] File checksum -- string/long
3. [ ] Maybe message (commit-alike about version) -- string
4. [ ] Patch ordered id -- long

## Patcher info (on local)

1. [ ] Current patch id (index in DB array)

## Three steps test patch (all base variants)

1. [ ] File created: (empty) -> (test_old.txt)
2. [x] File updated: (test_old.txt) -> (test_new.txt)
3. [ ] File deleted: (test_new.txt) -> (empty)

## Patch basic pipeline

1. [x] Check checksum
2. [ ] Check files bitwise
