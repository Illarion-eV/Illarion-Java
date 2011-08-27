require("questsystem.base")
module("MoveItemBeforeMove", package.seeall)

-- category: item
-- An item will be moved -- Ein Gegenstand soll bewegt werden

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte
local RADIUS = INTEGER -- Radius -- Radius
local TEXT_DE = TEXT -- German Text after movement -- Deutscher Text nach Bewegung
local TEXT_EN = TEXT -- English Text after movement -- Englischer Text nach Bewegung

function MoveItemBeforeMove(player, item, itemAfter)
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
