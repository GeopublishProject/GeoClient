package com.geopublish.geoclient.ui.markers;

/**
 * Created by edgar on 11/19/2015.
 * Guarda principalmente el estado de los iconos o llamadas de usuario
 */
public class StateItem {

        public ColorMarker colorMarker;
        public long id;
        public Boolean isItemSelected;

        public StateItem(long id, ColorMarker colorMarker) {

            this.isItemSelected = false;
            this.id = id;
            this.colorMarker=colorMarker;
        }

        @Override
        public String toString() {
            return this.colorMarker.colorName;
        }

}
