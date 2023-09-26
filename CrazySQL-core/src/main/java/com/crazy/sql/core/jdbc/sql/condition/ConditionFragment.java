package com.crazy.sql.core.jdbc.sql.condition;

import com.crazy.sql.core.enums.RelationShip;
import com.crazy.sql.core.jdbc.sql.SqlFragment;

public abstract class ConditionFragment implements SqlFragment {
    private RelationShip relationShip=RelationShip.AND;

    public RelationShip getRelationShip() {
        return relationShip;
    }

    public void setRelationShip(RelationShip relationShip) {
        this.relationShip = relationShip;
    }
}
