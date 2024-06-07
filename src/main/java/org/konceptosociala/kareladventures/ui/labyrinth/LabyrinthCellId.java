package org.konceptosociala.kareladventures.ui.labyrinth;

import java.util.Objects;
import java.util.regex.Pattern;

import org.konceptosociala.kareladventures.ui.InvalidCellIdException;

import lombok.Getter;

@Getter
public class LabyrinthCellId {
    public static final Pattern LABYRINTH_CELL_REGEX = Pattern.compile("lab_cell_([1-4])_([1-4])");
    private final int column;
    private final int row;

    public LabyrinthCellId(String id) throws InvalidCellIdException {
        var matcher = LABYRINTH_CELL_REGEX.matcher(id);

        if (matcher.matches()) {
            column = Integer.parseInt(matcher.group(1)); 
            row = Integer.parseInt(matcher.group(2));
        } else {
            throw new InvalidCellIdException(id);
        }
    }

    @Override
    public String toString() {
        return "lab_cell_"+column+"_"+row;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabyrinthCellId id = (LabyrinthCellId) o;
        return column == id.column && row == id.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }
}
