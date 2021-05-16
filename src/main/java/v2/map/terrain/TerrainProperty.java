package v2.map.terrain;


import v2.core.exceptions.TerrainPropertyWeightException;
import v2.graphics.Color;

public class TerrainProperty {

    private String descriptor;
    private TerrainPropertyList parent;

    private int relativeWeight;
    private float absoluteWeight;
    private final boolean fixedWeight;

    private final Color inputColor;
    private final Color originalColor;
    private final Color modifiedColor;

    public TerrainProperty(String descriptor, int relativeWeight, Color color) throws TerrainPropertyWeightException {

        if (relativeWeight < 0)
            throw new TerrainPropertyWeightException("TerrainProperty constructor. Invalid relativeWeight");

        this.descriptor = descriptor;
        this.relativeWeight = relativeWeight;
        this.absoluteWeight = 0;
        this.fixedWeight = false;
        this.originalColor = color;
        this.modifiedColor = Color.copy(color);
        this.inputColor = Color.copy(color);

    }

    public TerrainProperty(String descriptor, float absoluteWeight, Color color) throws TerrainPropertyWeightException {

        if (absoluteWeight > 1 || absoluteWeight <= 0)
            throw new TerrainPropertyWeightException("TerrainProperty constructor. Invalid absoluteWeight");

        this.descriptor = descriptor;
        this.relativeWeight = 0;
        this.absoluteWeight = absoluteWeight;
        this.fixedWeight = true;
        this.originalColor = color;
        this.modifiedColor = Color.copy(color);
        this.inputColor = Color.copy(color);

    }

    protected boolean hasFixedWeight() {
        return fixedWeight;
    }

    protected float absoluteWeight() {
        return absoluteWeight;
    }

    protected void setAbsoluteWeight(float absoluteWeight) {
        this.absoluteWeight = absoluteWeight;
    }

    protected int relativeWeight() {
        return relativeWeight;
    }

    protected void setParent(TerrainPropertyList parent) {
        this.parent = parent;
    }

    public TerrainPropertyList parent() {
        return parent;
    }

    // todo: (29/04/21) I could try to make "rarity" a field-variable. But in doing so, i'd have to
    //  change the Terrain-structure. This is not a high priority, as this would / should not be called more
    //  than once / application-cycle. Color on the other hand, must exist as field-variables.
    //  otherwise, cascading color multiplication would happen for all visible tiles' draw-call.

    public float rarity() {

        float rarity = this.absoluteWeight;
        TerrainPropertyList parent = this.parent;

        while(parent != null) {
            rarity *= parent.absoluteWeight();
            parent = parent.parent();
        }
        return rarity;
    }

    public void adjustRelativeWeight(int amount) {

        if (amount != 0 && !hasFixedWeight()) {

            relativeWeight += amount;

            if (relativeWeight < 0)
                relativeWeight = 0;

            if (parent != null)
                parent.reBalanceWeights(amount);
        }
    }

    public String descriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    // ----------------------------------------------------------------------------

    // COLORING

    protected void adjustColor() {

        modifiedColor.set(
                originalColor).mix(
                        inputColor);

        mixWithParents();
    }

    public void adjustColor(Color color) {

        modifiedColor.set(
                originalColor).mix(
                        inputColor.set(
                                color));

        mixWithParents();
    }

    public void resetToOriginalColor() {

        modifiedColor.set(
                inputColor.set(
                        originalColor));

        mixWithParents();
    }

    public void newOriginalColor(Color color, boolean clearInputColor) {

        if (clearInputColor) {

            modifiedColor.set(
                    inputColor.set(
                            originalColor.set(
                                    color)));
        }
        else {
            modifiedColor.set(
                    originalColor.set(
                            color)).mix(
                                    inputColor);
        }
        mixWithParents();
    }

    private void mixWithParents() {

        TerrainPropertyList parent = this.parent;

        while(parent != null) {

            modifiedColor.mix(
                    parent.modifiedColor(),
                    parent().colorInfluence());

            parent = parent.parent();
        }

    }


    public Color modifiedColor() {
        return modifiedColor;
    }

    public Color originalColor() {
        return originalColor;
    }


}
