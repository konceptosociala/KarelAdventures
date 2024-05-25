package org.konceptosociala.kareladventures.ui.inventory_cell_id;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class InventoryCellId {
    public static final Pattern GRID_CELL_REGEX = Pattern.compile("inv_cell_([1-4])_([1-5])");
    public static final Pattern NAMED_CELL_REGEX = Pattern.compile("inv_cell_(helmet|chestplate|leggings|boots|weapon)");
    private Optional<GridCell> gridCell = Optional.empty();
    private Optional<NamedCell> namedCell = Optional.empty();

    public InventoryCellId(String id) throws InvalidCellIdException {
        var gridMatcher = GRID_CELL_REGEX.matcher(id);
        var namedMatcher = NAMED_CELL_REGEX.matcher(id);

        if (gridMatcher.matches()) {
            this.gridCell = Optional.of(new GridCell(
                Integer.parseInt(gridMatcher.group(1)), 
                Integer.parseInt(gridMatcher.group(2))
            ));
        } else if (namedMatcher.matches()) {
            this.namedCell = Optional.of(NamedCell.valueOf(namedMatcher.group(1)));
        } else {
            throw new InvalidCellIdException(id);
        }
    }

    @Override
    public String toString() {
        if (gridCell.isPresent())
            return "inv_cell_"+gridCell.get();
        else 
            return "inv_cell_"+namedCell.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryCellId that = (InventoryCellId) o;
        return Objects.equals(gridCell, that.gridCell) && Objects.equals(namedCell, that.namedCell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gridCell, namedCell);
    }

    @Getter
    @RequiredArgsConstructor
    public class GridCell {
        private final int column;
        private final int row;

        @Override
        public String toString() {
            return column+"_"+row;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridCell gridCell = (GridCell) o;
            return column == gridCell.column && row == gridCell.row;
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, row);
        }
    }

    @Getter
    public enum NamedCell {
        helmet,
        chestplate,
        leggings,
        boots,
        weapon;

        @Override
        public String toString() {
            return this.name();
        }
    }
}
