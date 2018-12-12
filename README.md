# Mimir assignment downloader

Downloads Mimir assignments for a course and converts them to HTML.

## Download

See [current release](https://github.com/camann9/MimirDownloader/releases/latest).

## Run

    java -jar target/MimirDownloader-jar-with-dependencies.jar [OPTIONS] <course URL copied from browser> <target folder>

Use -h to get list of options.

### Modes

Mimir downloader supports a multi-file and a single-file mode. In multi-file mode the user specifies an output directory. The downloader will create an index.html file with links to every assignment in the course and an output HTML file for each assignment. In single-file mode the downloader will dump all questions into a single file (independent of the assignment they are in). The single-file mode uses a reduced assignment format that doesn't contain all the details (e.g. no code for test cases).

## Build

    mvn package
