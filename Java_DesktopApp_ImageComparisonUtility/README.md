# Image Comparison Utility
This utility is used to compare two images at a time and respond with difference percentage between them.

### Input
This utility allow user to select CSV file in a structure like below with absolute path to images.


| image1 | image2 |
| ------------- | ------------- |
| a.jpg  | b.jpg  |
| c.jpg  | d.jpg  |

### Output
This utility will create an output CSV file based on input CSV file in a structure like below.

| image1 | image2 | similar | elapsed | 
| ------------- | ------------- | ------------- | ------------- |
| a.jpg  | b.jpg  | 0 | 0.004 |
| c.jpg  | d.jpg  | 15 | 0.003 |

## Documentation

* [User Manual](https://github.com/mkthakral/ImageComparisionTool/blob/master/ImageComparision/resources/document/User_Manual.pdf) This document detailed information about how to setup and use this utility.
* [Developer Manual](https://github.com/mkthakral/ImageComparisionTool/blob/master/ImageComparision/resources/document/Developer_Manual.pdf) This document contains detailed information about setting up the code base, modify it and maintain it. It also has details about how the comparision algorithm functions.

## Programming Language

This application is built using Java, version 1.8.0_231

## System Requirments

This utilty is tested on following operatin systems with JRE 1.8.0_231

* Windows 10
* Mac OS Mojave 10.14.6
