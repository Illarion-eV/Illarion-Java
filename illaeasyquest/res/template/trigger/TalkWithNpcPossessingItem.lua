require("questsystem.base")
module("TalkWithNpcPossessingItem", package.seeall)

-- category: npc
-- Having an item while talking to an NPC-- Einen Gegenstand besitzen und mit einem NPC sprechen

local QUEST_NUMBER = 0
local PRECONDITION_QUESTSTATE = 0
local POSTCONDITION_QUESTSTATE = 0

local ITEM_ID = INTEGER -- ID of item to be possessed -- ID des zu besitzenden Gegenstandes
local ITEM_AMNT = INTEGER -- Amount of that item -- Anzahl dieser Gegenstände
local NPC_TRIGGER_DE = TEXT -- German trigger text -- Auslösender deutscher Text
local NPC_TRIGGER_EN = TEXT -- English trigger text -- Auslösender englischer Text
local NPC_REPLY_DE = TEXT -- German reply text (has item) -- Deutscher Antworttext (hat Gegenstand)
local NPC_REPLY_EN = TEXT -- English reply text (has item) -- Englischer Antworttext (hat Gegenstand)
local NPC_NOITEM_DE = TEXT -- German reply text (doesn't have item) -- Deutscher Antworttext (hat Gegenstand nicht)
local NPC_NOITEM_EN = TEXT -- English reply text (doesn't have item) -- Englischer Antworttext (hat Gegenstand nicht)

function receiveText(npc, type, text, PLAYER)
    if PLAYER:getType() == Character.player
    and ADDITIONALCONDITIONS(PLAYER)
    and questsystem.base.fulfilsPrecondition(PLAYER, QUEST_NUMBER, PRECONDITION_QUESTSTATE) then
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
            if PLAYER:countItem(ITEM_ID)>=ITEM_AMNT then
                npc:talk(Character.say, getNLS(PLAYER, NPC_REPLY_DE, NPC_REPLY_EN))
            
                HANDLER(PLAYER)
            
                questsystem.base.setPostcondition(PLAYER, QUEST_NUMBER, POSTCONDITION_QUESTSTATE)
        
                return true
            elseif (NPC_NOITEM_DE~="") then
                npc:talk(Character.say, getNLS(PLAYER, NPC_NOITEM_DE, NPC_NOITEM_EN))
          
                return true
            else
                return false
            end
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
