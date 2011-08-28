require("questsystem.base")
module("CharacterOnField", package.seeall)

-- category: item
-- Someone steps on an item -- Jemand steigt auf einen Gegenstand

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte
local TEXT_DE = TEXT -- German Use Text -- Deutscher Text beim Benutzen
local TEXT_EN = TEXT -- English Use Text -- Englischer Text beim Benutzen

function CharacterOnField( PLAYER )
  if PLAYER:isInRangeToPosition(POSITION,RADIUS)
      and questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then
    informNLS(PLAYER, TEXT_DE, TEXT_EN)
    questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
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
