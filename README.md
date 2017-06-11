## Key-value storage over file system
 
## Features
- file lock (guard for diferent jvm instances)
- read-write lock (guard inside jvm)

## Usage
Default usage looks like 

`new BlockingStorage(new DiskStorage(ROOT, TMP, keyValidator), new KeySpread());` 

## TODO
 - improve key validation
 - make key lock more agile (depending on entities' count)
 - improve disk storage (use subdirectories)
 - write performance tests