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

script
    : line* EOF
    ;

line
    : command? COMMENT* ( LINE_COMMENT | EOL )
    ;

command
    : configuration
    | talkCommand
    | textConfiguration
    ;

talkCommand
    : conditionList '->' consequenceList
    ;

conditionList
    : ( condition ',' )* trigger ( ',' condition )*
    ;

consequenceList
    : consequence ( ',' consequence )*
    ;

condition
    : trigger
    | 'isAdmin'
    | 'attrib' '(' attribute ')' compare advancedNumber
    | 'chance' '(' ( FLOAT | INT ) ')'
    | 'item' '(' itemId ',' itemPos ( ',' itemDataList )? ')' compare advancedNumber
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
    | 'deleteItem' '(' itemId ',' advancedNumber ( ',' itemDataList )? ')'
    | 'gemcraft'
    | 'inform' '(' STRING ')'
    | 'introduce'
    | 'item' '(' itemId ',' advancedNumber ( ',' itemQuality )? ( ',' itemDataList )? ')'
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
    | 'wrongLangDE' '=' STRING
    | 'wrongLangUS' '=' STRING
    ;

colorConfiguration
    : 'colorHair' '=' color
    | 'colorSkin' '=' color
    ;

equipmentConfiguration
    : 'itemChest' '=' itemId
    | 'itemCoat' '=' itemId
    | 'itemHands' '=' itemId
    | 'itemHead' '=' itemId
    | 'itemMainHand' '=' itemId
    | 'itemSecondHand' '=' itemId
    | 'itemShoes' '=' itemId
    | 'itemTrousers' '=' itemId
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
    : traderSimpleConfiguration
    | traderComplexConfiguration
    ;

traderSimpleConfiguration
    : ( 'sellItems' | 'buyPrimaryItems' | 'buySecondaryItems' ) '=' itemId ( ',' itemId )*?
    ;

traderComplexConfiguration
    : ( 'sellItem' | 'buyPrimaryItem' | 'buySecondaryItem' ) '=' traderComplexItemId ( ',' traderComplexEntry )*?
    ;

traderComplexEntry
    : 'de' '(' STRING ')'
    | 'en' '(' STRING ')'
    | 'price' '(' INT ')'
    | 'stack' '(' INT ')'
    | 'quality' '(' itemQuality ')'
    | 'data' '(' itemDataList ')'
    ;

traderComplexItemId
    : 'id' '(' itemId ')'
    ;

walkConfiguration
    : 'radius' '=' INT
    ;

textConfiguration
    : textKey STRING ',' STRING
    ;

textKey
    : 'cycletext'
    | 'hitPlayerMsg'
    | 'tradeFinishedMsg'
    | 'tradeFinishedWithoutTradingMsg'
    | 'tradeNotEnoughMoneyMsg'
    | 'tradeWrongItemMsg'
    | 'warpedMonsterMsg'
    | 'warpedPlayerMsg'
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
    : locationComponent ',' locationComponent ',' locationComponent
    ;

locationComponent
    : unop? INT
    ;

race
    : 'dwarf'
    | 'elf'
    | 'halfling'
    | 'human'
    | 'lizardman'
    | 'orc'
    ;

itemPos
    : 'all'
    | 'backpack'
    | 'belt'
    | 'body'
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
    | 'Free' | 'free'
    | 'None' | 'none'
    ;

rankedTown
    : 'Cadomyr'
    | 'Galmair'
    | 'Runewick'
    | STRING
    ;

itemId
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
    : '=' | '<' | '>' | '<=' | '>=' | '~='
    ;

unop
    : '-'
    ;

set
    : '=' | '+' | '-'
    ;

advancedNumber
    : INT
    | '%NUMBER'
    | advancedNumberExpression
    ;

advancedNumberExpression
    : 'expr' '(' advancedNumberExpressionBody ')'
    ;

advancedNumberExpressionBody
    : '(' advancedNumberExpressionBody ')'
    | advancedNumberExpressionBody luaCalcOp advancedNumberExpressionBody
    | luaCalcValue
    ;

luaCalcValue
    : unop? ('%NUMBER' | INT | FLOAT )
    ;

luaCalcOp
    : '+' | '-' | '*' | '/' | '%' | '^'
    ;

BOOLEAN
    : BOOLEAN_TRUE
    | BOOLEAN_FALSE
    ;

fragment
BOOLEAN_TRUE
    : 'true' | 'yes' | 'on'
    ;

fragment
BOOLEAN_FALSE
    : 'false' | 'no' | 'off'
    ;

FLOAT
    : Digit+ '.' Digit*
    | '.' Digit+
    ;

INT
    : Digit+
    ;

NAME
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

COMMENT
    : '--[[' .*? ']]'-> channel(HIDDEN)
    ;

LINE_COMMENT
    : '--' '['? ( ~( '[' | '\r' | '\n' ) ~( '\r' | '\n' )* )? EOL -> channel(HIDDEN)
    ;

STRING
    : '"' ( EscapeSequence | ~('\\'|'"') )* '"'
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