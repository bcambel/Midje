# Change Log
This project adheres to [Semantic Versioning](http://semver.org/).       
See [here](http://keepachangelog.com/) for the change log format.

## [1.9.0-SNAPSHOT]

- Experimental version from @dlebrero that unloads deleted namespaces.
- `(change-defaults :run-clojure-test false)` prevents clojure.test tests from being run.
- NOTE NOTE NOTE: Previous should be documented before 1.9.0 is released.

## [1.8.3] 
- Bump to newer versions of dependencies

## [1.8.2]
- Drop back to an older version of commons-codec to match what compojure uses.
  Will avoid annoying Maven messages for many users.

## [1.8.1]
- Messed up version in the project.clj file.

## [1.8.0]
- no longer indirectly drags in all of clojurescript.
- improved error messages when prerequisites are passed unrealized lazyseqs.
- obscure bug fixes

(Some other non-feature, cleanup changes were lost.)



---------------------

Older changes were in HISTORY.md, and they're not worth preserving.
