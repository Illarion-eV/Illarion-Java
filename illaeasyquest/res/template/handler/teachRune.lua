require("base.class")

module("handler.teachrune", package.seeall)

teachRune = base.class.class(function(tchrune, ply, mtype, rID)
    tchrune.player=posi;
    tchrune.magictype=mtype;
    tchrune.runeID=rID;
end);

function teachRune:execute()


    if (world:isItemOnField(self.pos)==true) then
        item=world:getItemOnField(self.pos);
        if (item.id==self.deleteItemId or self.deleteItemId==0) then
            world:erase(item,1);
            return 1;
        else
            return -2;
        end
    else
        return -1;
    end
end
