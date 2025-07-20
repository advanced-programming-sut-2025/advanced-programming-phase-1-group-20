package org.example.common.models.MapDetails;

import java.io.Serializable;

public class Well extends Building implements Serializable {
    public Well(int x, int y, String name, String type) {
        super(x, y, name, type);
    }
}
