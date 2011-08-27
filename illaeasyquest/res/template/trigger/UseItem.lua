require("questsystem.base")
module("UseItem", package.seeall)

-- category: item
-- Use an item -- Einen Gegenstand benutzen

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte
local RADIUS = NUMBER -- Radius -- Radius
local TEXT_DE = TEXT -- German Use Text -- Deutscher Text beim Benutzen
local TEXT_EN = TEXT -- English Use Text -- Englischer Text beim Benutzen

function UseItem( player, item, TargetItem, counter, Param, ltstate )
  if player:isInRangeToPosition(POSITION,RADIUS)
      and questsystem.base.fulfilsPrecondition(player, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then
    informNLS(player, TEXT_DE, TEXT_EN)
    questsystem.base.setPostcondition(player, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
    return true
  end

  return false
end

function informNLS(player, textDe, textEn)
  if player:getPlayerLanguage() == Player.german then
    player:inform(player, item, textDe)
  else
    player:inform(player, item, textEn)
  end
end
