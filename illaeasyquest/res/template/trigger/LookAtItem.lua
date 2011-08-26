require("questsystem.base")
module("LookAtItem", package.seeall)

-- category: item
-- Look at an item -- Einen Gegenstand ansehen

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local POSITION = POSITION -- Map position -- Position auf der Karte
local LOOKAT_TEXT_DE = TEXT -- German LookAt Text -- Deutscher Text beim Anschauen
local LOOKAT_TEXT_EN = TEXT -- English LookAt Text -- Englischer Text beim Anschauen

function LookAtItem(player, item)
  if item.pos == POSITION
      and questsystem.base.fulfilsPrecondition(player, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then
    itemInformNLS(player, item, LOOKAT_TEXT_DE, LOOKAT_TEXT_EN)
    questsystem.base.setPostcondition(player, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
    return true
  end

  return false
end

function itemInformNLS(player, item, textDe, textEn)
  if player:getPlayerLanguage() == Player.german then
    world:itemInform(player, item, textDe)
  else
    world:itemInform(player, item, textEn)
  end
end
