package com.hextilla.cardbox.server.persist;

import com.samskivert.depot.Key;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.annotation.Column;
import com.samskivert.depot.annotation.Entity;
import com.samskivert.depot.annotation.GeneratedValue;
import com.samskivert.depot.annotation.GenerationType;
import com.samskivert.depot.annotation.Id;
import com.samskivert.depot.expression.ColumnExp;
import com.samskivert.util.StringUtil;

@Entity(name="CARDS")
public class CardRecord extends PersistentRecord {

    // AUTO-GENERATED: FIELDS START
    public static final Class<CardRecord> _R = CardRecord.class;
    public static final ColumnExp<Integer> CARD_ID = colexp(_R, "cardID");
    public static final ColumnExp<Integer> NORTH_POWER = colexp(_R, "northPower");
    public static final ColumnExp<Integer> NORTH_EAST_POWER = colexp(_R, "northEastPower");
    public static final ColumnExp<Integer> SOUTH_EAST_POWER = colexp(_R, "southEastPower");
    public static final ColumnExp<Integer> SOUTH_POWER = colexp(_R, "southPower");
    public static final ColumnExp<Integer> SOUTH_WEST_POWER = colexp(_R, "southWestPower");
    public static final ColumnExp<Integer> NORTH_WEST_POWER = colexp(_R, "northWestPower");
    public static final ColumnExp<Integer> ELEMENT = colexp(_R, "element");
    // AUTO-GENERATED: FIELDS END

	public static final int SCHEMA_VERSION = 1;
    
    /** The card's unique integer identifier. */
    @Id @Column(name="CARD_ID") @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int cardID;
    
    /** The number associated with the top side of the card. */
    @Column(name="NORTH_POWER")
    public int northPower;

    /** The number associated with the top-left side of the card. */
    @Column(name="NORTH_EAST_POWER")
    public int northEastPower;
    
    /** The number associated with the bottom-left side of the card. */
    @Column(name="SOUTH_EAST_POWER")
    public int southEastPower;

    /** The number associated with the bottom side of the card. */
    @Column(name="SOUTH_POWER")
    public int southPower;
    
    /** The number associated with the bottom-right side of the card. */
    @Column(name="SOUTH_WEST_POWER")
    public int southWestPower;
    
    /** The number associated with the top-right side of the card. */
    @Column(name="NORTH_WEST_POWER")
    public int northWestPower;
    
    /** The element of the card. */
    @Column(name="ELEMENT")
    public int element;    
    
    /**
     * Provides a string representation of this instance.
     */
    @Override
    public String toString ()
    {
        return StringUtil.fieldsToString(this);
    }

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link CardRecord}
     * with the supplied key values.
     */
    public static Key<CardRecord> getKey (int cardID)
    {
        return newKey(_R, cardID);
    }

    /** Register the key fields in an order matching the getKey() factory. */
    static { registerKeyFields(CARD_ID); }
    // AUTO-GENERATED: METHODS END
}
