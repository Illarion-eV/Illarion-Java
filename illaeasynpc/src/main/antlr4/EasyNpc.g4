/*
 * This file is part of the Illarion easyNPC Editor.
 *
 * Copyright © 2014 - Illarion e.V.
 *
 * The Illarion easyNPC Editor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion easyNPC Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion easyNPC Editor.  If not, see <http://www.gnu.org/licenses/>.
 */
grammar EasyNpc;

line
    : statement? EOL
    ;

statement
    : COMMENT
    | command
    ;

command
    : configuration
    | conditionList '->' consequenceList
    | textKey STRING ',' STRING
    ;

conditionList
    : ( condition ',' )* trigger+ ( ',' condition )*
    ;

consequenceList
    : ( consequence ',' )* answer+ ( ',' consequence )*
    ;

condition
    : trigger
    | 'isAdmin'
    | 'attrib' '(' attribute ')' compare advancedNumber
    | 'chance' '(' ( FLOAT | INT ) ')'
    | 'item' '(' itemid ')' compare advancedNumber
    | language
    | 'magictype' '=' magictype
    | 'money' compare advancedNumber
    | '%NUMBER' compare INT
    | 'queststatus' '(' questId ')' compare advancedNumber
    | 'race' '=' race
    | 'rank' compare advancedNumber
    | 'sex' '=' gender
    | 'skill' '(' skill ')' compare advancedNumber
    | 'state' compare advancedNumber
    | 'talkMode' '=' talkMode
    | talkstateGet
    | 'town' '=' town
    ;

consequence
    : answer
    | 'arena' '(' arenaTask ')'
    | 'attrib' '(' attribute ')' set advancedNumber
    | 'deleteItem' '(' itemid ',' advancedNumber ( ',' itemDataList )? ')'
    | 'gemcraft'
    | 'inform' '(' STRING ')'
    | 'introduce'
    | 'item' '(' itemid ',' advancedNumber ( ',' itemQuality )? ( ',' itemDataList )? ')'
    | 'money' set advancedNumber
    | 'queststatus' '(' questId ')' set advancedNumber
    | 'rankpoints' set advancedNumber
    | 'repair'
    | 'rune' '(' magictypeWithRunes ',' INT ')'
    | 'skill' '(' skill ')' set advancedNumber
    | 'state' set advancedNumber
    | talkstateSet
    | 'town' '=' town
    | 'trade'
    | 'treasure' '(' advancedNumber ')'
    | 'warp' '(' location ')'
    ;

configuration
    : basicConfiguration
    | colorConfiguration
    | equipmentConfiguration
    | guardConfiguration
    | hairConfiguration
    | traderConfiguration
    | walkConfiguration
    ;

basicConfiguration
    : 'affiliation' '=' town
    | 'author' '=' STRING
    | 'autointroduce' '=' BOOLEAN
    | 'wrongLangDE' '=' STRING
    | 'wrongLangUS' '=' STRING
    | 'defaultLanguage' '=' charLanguage
    | 'direction' '=' direction
    | 'job' '=' STRING
    | 'language' '=' charLanguage
    | 'lookatDE' '=' STRING
    | 'lookatUS' '=' STRING
    | 'name' '=' STRING
    | 'position' '=' location
    | 'race' '=' race
    | 'sex' '=' gender
    | 'useMsgDE' '=' STRING
    | 'useMsgUS' '=' STRING
    ;

colorConfiguration
    : 'colorHair' '=' color
    | 'colorSkin' '=' color
    ;

equipmentConfiguration
    : 'itemChest' '=' itemid
    | 'itemCoat' '=' itemid
    | 'itemHands' '=' itemid
    | 'itemHead' '=' itemid
    | 'itemMainHand' '=' itemid
    | 'itemSecondHand' '=' itemid
    | 'itemShoes' '=' itemid
    | 'itemTrousers' '=' itemid
    ;

guardConfiguration
    : 'guardRange' '=' INT ',' INT ',' INT ',' INT
    | 'guardWarpTarget' '=' location
    ;

hairConfiguration
    : 'hairID' '=' INT
    | 'beardID' '=' INT
    ;

traderConfiguration
    : ( 'sellItems' | 'buyPrimaryItems' | 'buySecondaryItems' ) '=' itemid ( ',' itemid )*?
    | ( 'sellItem' | 'buyPrimaryItem' | 'buySecondaryItem' ) '=' traderComplexItemId ( ',' traderComplexEntry )*?
    ;

traderComplexEntry
    : 'de' '(' STRING ')'
    | 'en' '(' STRING ')'
    | 'price' '(' INT ')'
    | 'stack' '(' INT ')'
    | 'quality' '(' itemQuality ')'
    | 'data' '(' itemData ')'
    ;

traderComplexItemId
    : 'id' '(' itemid ')'
    ;

walkConfiguration
    : 'radius' '=' INT
    ;

textKey
    : 'cycletext'
    | 'warpedMonsterMsg'
    | 'warpedPlayerMsg'
    | 'hitPlayerMsg'
    | 'tradeNotEnoughMoneyMsg'
    | 'tradeFinishedMsg'
    | 'tradeFinishedWithoutTradingMsg'
    | 'tradeWrongItemMsg'
    ;

trigger
    : STRING
    ;

answer
    : STRING
    ;

arenaTask
    : 'requestMonster'
    | 'getStats'
    | 'getRanking'
    ;

attribute
    : 'agility'
    | 'constitution'
    | 'dexterity'
    | 'essence'
    | 'foodlevel'
    | 'hitpoints'
    | 'intelligence'
    | 'manapoints'
    | 'perception'
    | 'strength'
    | 'willpower'
    ;

color
    : colorComponent ',' colorComponent ',' colorComponent
    ;

colorComponent
    : INT
    ;

direction
    : 'north'
    | 'northeast'
    | 'east'
    | 'southeast'
    | 'south'
    | 'southwest'
    | 'west'
    | 'northwest'
    ;

language
    : 'german'
    | 'english'
    ;

charLanguage
    : 'ancient'
    | 'common'
    | 'dwarf'
    | 'elf'
    | 'halfling'
    | 'human'
    | 'lizard'
    | 'orc'
    ;

location
    : ( NEG_INT | INT ) ',' ( NEG_INT | INT ) ',' ( NEG_INT | INT )
    ;

race
    : 'dwarf'
    | 'elf'
    | 'halfling'
    | 'human'
    | 'lizardman'
    | 'orc'
    ;

gender
    : 'male'
    | 'female'
    ;

skill
    : NAME
    ;

talkMode
    : 'shout' | 'yell'
    | 'whisper'
    | 'talk' | 'say'
    ;

talkstateGet
     : 'busy'
     | 'idle'
     ;

talkstateSet
    : 'begin'
    | 'end'
    ;

town
    : rankedTown
    | 'Free'
    | 'None'
    ;

rankedTown
    : 'Cadomyr'
    | 'Galmair'
    | 'Runewick'
    | STRING
    ;

itemid
    : INT
    ;

itemQuality
    : INT
    ;

itemData
    : STRING '=' STRING
    ;

itemDataList
    : itemData ( ',' itemData )*
    ;

questId
    : INT
    ;

magictype
    : magictypeWithRunes | 'nomagic'
    ;

magictypeWithRunes
    : 'bard' | 'druid' | 'mage' | 'priest'
    ;

compare
    : '=' | '<' | '>' | '<=' | '>=' | '~=' | '!=' | '<>'
    ;

set
    : '=' | '+=' | '-='
    ;

advancedNumber
    : INT
    | '%NUMBER'
    | NUMBER_EXPRESSION
    ;

BOOLEAN
    : BOOLEAN_TRUE
    | BOOLEAN_FALSE
    ;

BOOLEAN_TRUE
    : 'true' | 'yes' | 'on'
    ;

BOOLEAN_FALSE
    : 'false' | 'no' | 'off'
    ;

FLOAT
    : Digit+ '.' Digit*
    | '.' Digit+
    ;

NEG_INT
    : '-' INT
    ;

INT
    : Digit+
    ;

NAME
    : [A-Za-z][A-Za-z ]*?
    ;

COMMENT
    : '--' ~('\n'|'\r')* -> channel(HIDDEN)
    ;

STRING
    : '"' ( EscapeSequence | ~('\\'|'"') )* '"'
    ;

NUMBER_EXPRESSION
    : 'expr' '(' NUMBER_EXPRESSION_BODY ')'
    ;

NUMBER_EXPRESSION_BODY
    : ('%NUMBER' | Digit+) ( [+-*/^%]+ ('%NUMBER' | Digit+) )*
    ;

fragment
EscapeSequence
    : '\\' [abfnrtvz"'\\]
    ;

fragment
Digit
    : [0-9]
    ;

EOL
    : '\r'? '\n'
    ;

WS
    : [ \t\u000C]+ -> skip
    ;