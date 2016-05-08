package game;

/**
 * Created by Max on 3/19/2016.
 *
 * ToDo: Create a location interface to be shared among classes.
 *
 * A Location interface could be shared between Point, Creature, and Item which could implement it. That way an item's
 * location could be a point in the world, a creature that's carrying it (or it's point in the world), or a container
 * it's in. That would also be useful because an item would have a reference to wherever it is and whoever is carrying
 * it. We could have the owner update their location and add another flag indicating if the item is on the floor, in a
 * container, or being carried. Sounds cumbersome and unnecessary; best to do without for now.
 */
public interface Location {
}
