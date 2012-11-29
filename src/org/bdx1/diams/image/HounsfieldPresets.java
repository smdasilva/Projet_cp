package org.bdx1.diams.image;

public enum HounsfieldPresets {

    Air("Air", 0, 50),
    Lung("Lung", 500, 200),
    SoftTissue("Soft Tissue", 1060, 40),
    Fat("Fat", 980, 40),
    Water("Water", 1024, 100),
    CSF("CSF", 1040, 500),
    Blood("Blood", 1065, 300),
    Muscle("Muscle", 1065, 500  ),
    Bones("Bones", 1700,600);
    
    private final String name;
    private final int windowCenter;
    private final int windowWidth;
    
    
    HounsfieldPresets(String name, int center, int width) {
        this.name = name;
        this.windowCenter = center;
        this.windowWidth = width;
    }
    
    public String getName() {return this.name; }
    public int getCenter() { return this.windowCenter; }
    public int getWidth() { return this.windowWidth; }
    public String toString() { return this.getName(); }
}
