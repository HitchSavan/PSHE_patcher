# TODO and algorythms info

## DB fields

1. All in ordered array -- array/table
2. Timestamp of patch creation -- timestamp
   - 2.1. Filename (w/ relative path) -- string
   - 2.2. Ordered array of changes -- array/table
     - 2.2.1. Mod type (added/removed) -- bool
     - 2.2.2. Mod start index (byte pos in file) -- longboi int
     - 2.2.3. Mod end index -- longboi int
   - 2.3. File checksum -- string/long
3. Maybe message (commit-alike about version) -- string

## Patcher info (on local)

1. Current patch id (index in DB array)

## Three steps test patch (all base variants)

1. File created: (empty) -> (test_old.txt)
2. File updated: (test_old.txt) -> (test_new.txt)
3. File deleted: (test_new.txt) -> (empty)

## Patch basic pipeline

1. Check checksum
2. Check files bitwise
