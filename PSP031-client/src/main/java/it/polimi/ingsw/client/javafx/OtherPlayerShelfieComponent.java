package it.polimi.ingsw.client.javafx;

import it.polimi.ingsw.model.PlayerView;

/**
 * Ccomponent containing the shelfie of a remote player
 *
 * @see PlayerShelfieComponent
 */
class OtherPlayerShelfieComponent extends PlayerShelfieComponent {
    public OtherPlayerShelfieComponent(FxResourcesLoader resources, PlayerView player) {
        super(resources, player, true, true);
    }
}
