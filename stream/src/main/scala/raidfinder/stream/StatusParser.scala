package raidfinder.stream

import raidfinder.protocol.domain._
import twitter4j._

object StatusParser {
    val raidRegexEn = "((?s).*)([0-9A-F]{8}) :Battle ID\nI need backup!\n(.+)\n?(.*)".r
    val raidRegexJp = "((?s).*)([0-9A-F]{8}) :参戦ID\n参加者募集！\n(.+)\n?(.*)".r
    val bossRegex = "Lv(?:l )?([0-9]+) (.*)".r
    val gbfSource = """<a href="http://granbluefantasy.jp/" rel="nofollow">グランブルー ファンタジー</a>"""

    def parse(status: Status): Option[RaidTweet] = status.getText match {
        case _ if status.getSource != gbfSource => None
        case raidRegexEn(text, raidId, raidName @ bossRegex(raidLvl, _), _) =>
            Some(RaidTweet(raidId, text, status.getCreatedAt(), raidName, raidLvl.toInt, English))
        case raidRegexJp(text, raidId, raidName @ bossRegex(raidLvl, _), _) =>
            Some(RaidTweet(raidId, text, status.getCreatedAt(), raidName, raidLvl.toInt, Japanese))
        case _ => None
    }
}
