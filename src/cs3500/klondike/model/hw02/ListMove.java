package cs3500.klondike.model.hw02;

import java.util.Collection;
import java.util.List;

/**
 * Utility class containing methods for moving elements from lists to collections.
 */
public class ListMove {
  /**
   * Move the first element of list srcList to  collection destList.
   * @param srcList non-empty list to move the first element from
   * @param destList list to move the first element to
   * @param <T> Object contained by the list
   * @throws IllegalArgumentException if srcList is empty or either argument is null
   */
  public static <T> void moveFirstToCollection(List<T> srcList, Collection<T> destList) {
    if (srcList == null || destList == null || srcList.isEmpty()) {
      throw new IllegalArgumentException("Both arguments must be lists,"
          + "and source list must be non-empty");
    }
    destList.add(srcList.get(0));
    srcList.remove(0);
  }

  /**
   * Move the last element of list srcList to the collection destList.
   * @param srcList non-empty list to move the first element from
   * @param destList collection to move the last element to
   * @param <T> Object contained by the list
   * @throws IllegalArgumentException if srcList is empty or either argument is null
   */
  public static <T> void moveLastToCollection(List<T> srcList, Collection<T> destList) {
    if (srcList == null || destList == null || srcList.isEmpty()) {
      throw new IllegalArgumentException("Both arguments must be lists,"
          + "and source list must be non-empty");
    }
    T toMove = srcList.get(srcList.size() - 1);
    if (toMove == null) {
      throw new IllegalStateException("card to move is null");
    }
    destList.add(toMove);
    srcList.remove(srcList.size() - 1);
  }

  /**
   * Move the last numElements from list srcList to the end of list destList preserving order.
   * @param numElements positive number of elements to move
   * @param srcList source list containing at least numElements elements
   * @param destList list to move elements to
   * @param <T> type of elements in lists
   * @throws IllegalArgumentException if either list is null
   * @throws IllegalArgumentException if srcList does not have enough elements
   * @throws IllegalArgumentException if numElements is not positive
   */
  public static <T> void moveLastN(int numElements, List<T> srcList, List<T> destList) {
    if (numElements < 1) {
      throw new IllegalArgumentException("numElements must be a positive integer");
    }
    if (srcList == null || destList == null || srcList.size() < numElements) {
      throw new IllegalArgumentException("Lists must be non-null and source"
                                        + "list must have enough elements to move");
    }

    for (int i = numElements; i > 0; i--) {
      destList.add(destList.size(), srcList.get(srcList.size() - i));
      srcList.remove(srcList.size() - i);
    }
  }
}
