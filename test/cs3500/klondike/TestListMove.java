package cs3500.klondike;

import cs3500.klondike.model.hw02.ListMove;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tester class for ListMove utility class.
 */
public class TestListMove {
  List<Integer> onesList;

  List<Integer> twosList;

  List<Integer> emptyList;
  Stack<Integer> emptyStack;

  @Before
  public void init() {
    onesList = new ArrayList<>(Arrays.asList(0,1,2,3,4,5));
    twosList = new ArrayList<>(Arrays.asList(2,3,4));
    emptyList = new ArrayList<>();
    emptyStack = new Stack<>();
  }

  @Test
  public void testMoveFirst() {
    init();
    ListMove.moveFirstToCollection(onesList, emptyStack);
    Assert.assertEquals(Arrays.asList(1,2,3,4,5), onesList);
    Assert.assertEquals(emptyStack.peek(), new Stack<>().push(0));

    init();
    Assert.assertThrows(IllegalArgumentException.class,
        () -> ListMove.moveFirstToCollection(emptyList, onesList));

    init();
    ListMove.moveFirstToCollection(onesList, onesList);
    Assert.assertEquals(onesList, new ArrayList<>(Arrays.asList(1,2,3,4,5,0)));
  }

  @Test
  public void testMoveLast() {
    init();
    ListMove.moveLastToCollection(onesList, emptyStack);
    Assert.assertEquals(Arrays.asList(0,1,2,3,4), onesList);
    Assert.assertEquals(emptyStack.peek(), new Stack<>().push(5));
    ListMove.moveLastToCollection(onesList, twosList);
    Assert.assertEquals(Arrays.asList(0,1,2,3), onesList);
    Assert.assertEquals(twosList, new ArrayList<>(Arrays.asList(2,3,4,4)));

    init();
    Assert.assertThrows(IllegalArgumentException.class,
        () -> ListMove.moveLastToCollection(emptyList, onesList));
  }

  @Test
  public void testMoveLastN() {
    init();
    ListMove.moveLastN(3, onesList, twosList);
    Assert.assertEquals(onesList, new ArrayList<>(Arrays.asList(0,1,2)));
    Assert.assertEquals(twosList, new ArrayList<>(Arrays.asList(2,3,4,3,4,5)));

    init();
    ListMove.moveLastN(3, twosList, onesList);
    Assert.assertTrue(twosList.isEmpty());
    Assert.assertEquals(onesList, new ArrayList<>(Arrays.asList(0,1,2,3,4,5,2,3,4)));

    init();
    Assert.assertThrows(IllegalArgumentException.class,
        () -> ListMove.moveLastN(1, emptyList, onesList));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> ListMove.moveLastN(1, null, onesList));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> ListMove.moveLastN(1, onesList, null));
    Assert.assertThrows(IllegalArgumentException.class,
        () -> ListMove.moveLastN(0, onesList, twosList));
  }
}
