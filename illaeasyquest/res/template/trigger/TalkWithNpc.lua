require("questsystem.base")
module("TalkWithNpc", package.seeall)

-- category: npc
-- Talk with an NPC -- Mit einem NPC sprechen

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local NPC_TRIGGER_DE = TEXT -- German trigger text -- Auslösender deutscher Text
local NPC_TRIGGER_EN = TEXT -- English trigger text -- Auslösender englischer Text
local NPC_REPLY_DE = TEXT -- German reply text -- Deutscher Antworttext
local NPC_REPLY_EN = TEXT -- English reply text -- Englischer Antworttext

function receiveText(type, text, player)
  if questsystem.base.fulfilsPrecondition(player, QUEST_NUMBER, PRECONDITION_QUESTSTATE)
      and player:getType() == Character.player
      and string.find(text, getNLS(player, NPC_TRIGGER_DE, NPC_TRIGGER_EN)) then
    thisNPC:talk(Character.say, getNLS(player, NPC_REPLY_DE, NPC_REPLY_EN))
    questsystem.base.setPostcondition(player, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)

    return true
  end

  return false
end

function getNLS(player, textDe, textEn)
  if player:getPlayerLanguage() == Player.german then
    return textDe
  else
    return textEn
  end
end
