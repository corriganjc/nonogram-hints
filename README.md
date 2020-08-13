# nonogram-hints
A tool for generating the hints for nonograms from an array representing a black and white image.

Nonograms are a form of logic puzzle where an image needs to be reconstructed based on the counts of runs of pixels in each row and column of the image. More details can be found in the Wikipedia [article](https://en.wikipedia.org/wiki/Nonogram). Nonograms are worth studying because they are a deceptively difficult puzzle. They seem to be an ideal candidate for [Finite Domain Constraint Logic Programming](https://en.wikipedia.org/wiki/Constraint_logic_programming). They can also be a good application for studying combinatorics and graph theory. An interesting survey of solution algorithms is provided in [this article](http://fse.studenttheses.ub.rug.nl/15287/1/Master_Educatie_2017_RAOosterman.pdf). This project doesn't attempt to solve nonograms, but rather, it can be used to generate the hints, given a black and white image. As such it is useful for generating input for solvers. As images with sparse figure pixels (i.e., on or 1 pixels) can have ambiguous solutions, this program provides a switch for generating hints based on the ground pixels (i.e., off or 0 pixels).

## Building

This project builds using [Leiningen](https://leiningen.org/).

`lein uberjar`

## Usage

There are two ways to run the code:

`lein run [-g] [-t JSON|PNG] <path to input file>`

Or:

`java -jar nonogram-hints-0.1.0-SNAPSHOT-standalone.jar [-g] [-t JSON|PNG]  <path to input file>`
