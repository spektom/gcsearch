# gcsearch #

The aim of this project is to provide intuitive interface for searching public source code
via Google Code Search directly from your lovely Eclipse IDE.

## Installation ##

You must have Eclipse 3.5 and greater.

 * Go to Help -> Install New Software...
 * Type update site: https://github.com/spektom/gcsearch/raw/master/update/
 * Press ENTER.
 * Click on Select All, then proceed to the next page.
 * Click on Next again.
 * Accept the license agreement.
 * Click on Finish, and follow the instructions.

## Usage ##

Once installed, the feature integrates into exiting search dialog (press CTRL+H):

![Opening search dialog](https://raw.github.com/spektom/gcsearch/master/docs/usage1.png)

Search results are opened in the standard Eclipse search results view:

![Observing search results](https://raw.github.com/spektom/gcsearch/master/docs/usage2.png)


## Source retrieval rules ##

### Introduction ###

Source retrieval rules help re-construct URL that brings you to the file containing the source code of the search result.

### Details ###

Source retrieval rule consists of two parts:

**Package pattern** - Regular expression that matches one of search result package names.

**Target URL** - Replacement string that represents the URL to the file containing the source code. The string may contain the following placeholders:

 * %FILE% - Search result file name (will be replaced with the actual one)
 * $1, $2, etc... - Match group of the package pattern.

### Editing Rules ###

To edit existing source retrieval rules, either go to Window -> Preferences -> General -> Search -> Google Code, or click on "Configure search settings" button placed in the Search Results View toolbar:

![Configuring source retrieval rules](https://raw.github.com/spektom/gcsearch/master/docs/conf1.jpg)

Configuration screen:

![Configuring source retrieval rules](https://raw.github.com/spektom/gcsearch/master/docs/conf2.jpg)


### Examples ###

#### Handling GitHub URL ####

This rule helps to convert git:// URL to HTTP (for github.com host only):

**Package pattern**: `git://github\.com/(.*)\.git`
**Target URL**: `http://github.com/$1/raw/master/%FILE%`

