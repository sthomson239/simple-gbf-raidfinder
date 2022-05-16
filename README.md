# Simple GBF Raidfinder

This project aims to build a simple raidfinder for the game [Granblue Fantasy](http://granbluefantasy.jp).
A raidfinder is a utility tool used by players to easily find raid codes that are posted to Twitter in real time.
It can be accessed from [Here](http://simple-gbf-raidfinder.herokuapp.com/)

![Screenshot](https://imgur.com/LWBhOjR.png)

## Currently Supported Features

* Click on available raid IDs to copy to clipboard.
* Using the "Follow/Unfollow" menu, select one or more raid bosses to "follow" it and see all future raid IDs that
appear on Twitter under that specific raid.
* English and Japanese raid tweets of the same raid boss are gathered under a single English name.


## Notes

* Many low level raids within the game are usually unpopular and so raid IDs will appear for these very infequently.
If you wish to test the raidfinder, try more popular higher level raids such as "Lvl 200 Akasha" or "Lvl 150 Proto Bahamut".
* In order to gather English and Japanese raid tweets under the same name, raid boss information is entered manually into
a JSON file. Any newly introduced raids that do not appear here will appear under their Japanese name until the file
is updated.


## TODO

* Implement popup message when clicking on and copying raid IDs.
* Implement filtering of raid bosses using a search bar or by selecting filter terms.
* Implement optional notification sounds for followed raid bosses.

