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

function receiveText(type, text, PLAYER)
    if questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE)
    and player:getType() == Character.player then
        if PLAYER:getPlayerLanguage() == Player.german then
            NPC_TRIGGER=string.gsub(NPC_TRIGGER_DE,'([ ]+)',' .*');
        else
            NPC_TRIGGER=string.gsub(NPC_TRIGGER_EN,'([ ]+)',' .*');
        end

        foundTrig=false
        
        for word in string.gmatch(NPC_TRIGGER, "[^|]+") do 
            if string.find(text,word)~=nil then
                foundTrig=true
            end
        end

        if foundTrig then
      
            thisNPC:talk(Character.say, getNLS(PLAYER, NPC_REPLY_DE, NPC_REPLY_EN))
            
            HANDLER()
            
            questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
        
            return true
        end
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
