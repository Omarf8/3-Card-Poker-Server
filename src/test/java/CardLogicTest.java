import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class CardLogicTest {
    Deck deck;
    ArrayList<Card> hand;

    @BeforeEach
    void setUp() {
        deck = new Deck();
        hand =  new ArrayList<>();
    }

    /*------------------------------------------------------------
                        TESTING THE DECK CLASS
     -------------------------------------------------------------*/

	@Test
	void rankTest() {
        int rank = 2;
        for(int i = 0; i < 13; i++) {
            assertEquals(rank, deck.getDeck().get(i).getRank(), "Card at index " + i + " does not equal rank " + rank);
            rank++;
        }

        rank = 2;
        for(int i = 13; i < 26; i++) {
            assertEquals(rank, deck.getDeck().get(i).getRank(), "Card at index " + i + " does not equal rank " + rank);
            rank++;
        }

        rank = 2;
        for(int i = 26; i < 39; i++) {
            assertEquals(rank, deck.getDeck().get(i).getRank(), "Card at index " + i + " does not equal rank " + rank);
            rank++;
        }

        rank = 2;
        for(int i = 39; i < 52; i++) {
            assertEquals(rank, deck.getDeck().get(i).getRank(), "Card at index " + i + " does not equal rank " + rank);
            rank++;
        }
    }

    @Test
    void suitTest() {
        char suit = 'H';
        for(int i = 0; i < 13; i++) {
            assertEquals(suit, deck.getDeck().get(i).getSuit(), "Card at index " + i + " does not equal suit " + suit);
        }

        suit = 'C';
        for(int i = 13; i < 26; i++) {
            assertEquals(suit, deck.getDeck().get(i).getSuit(), "Card at index " + i + " does not equal suit " + suit);
        }

        suit = 'D';
        for(int i = 26; i < 39; i++) {
            assertEquals(suit, deck.getDeck().get(i).getSuit(), "Card at index " + i + " does not equal suit " + suit);
        }

        suit = 'S';
        for(int i = 39; i < 52; i++) {
            assertEquals(suit, deck.getDeck().get(i).getSuit(), "Card at index " + i + " does not equal suit " + suit);
        }
    }

    @Test
    void pathnameTest() {
        int index = 1;
        for(int i = 0; i < 52; i++) {
            assertEquals("8BitDeck" + index + ".png", deck.getDeck().get(i).pathName, "Card at index " + i + " does not equal pathname 8BitDeck" + index + ".png");
            index++;
        }
    }

    /*------------------------------------------------------------
                TESTING THE THREECARDLOGIC CLASS
     -------------------------------------------------------------*/

    @Test
    void evalHandHighCardTest() {
        // Create 3 cards with different suits
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 4, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        HashSet<Integer> ranks = new HashSet<>(); // Used to have 3 unique numbers
        ArrayList<Integer> ranksUsed = new ArrayList<>(); // Used to set the ranks for each card using random ranks

        for(int i = 0; i < 25; i++) {
            // Generate 3 numbers from 2-14
            while(ranks.size() < 3) {
                int rand = (int)(Math.random() * 13 + 2);
                // We should make sure the generated numbers are not next to each other because we don't want to make straights
                if(!ranks.contains(rand) && !ranksUsed.contains(rand + 1) && !ranksUsed.contains(rand - 1)) {
                    ranks.add(rand);
                    ranksUsed.add(rand);
                }
            }
            // Use the 3 generated ranks
            c1.setRank(ranksUsed.get(0));
            c2.setRank(ranksUsed.get(1));
            c3.setRank(ranksUsed.get(2));
            deck.sortHand(hand);
            assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a HIGH_CARD but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Clear the integers used
            ranks.clear();
            ranksUsed.clear();
        }
    }

    @Test
    void evalHandPairTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 4, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        HashSet<Integer> ranks = new HashSet<>(); // Used to have 2 unique numbers
        ArrayList<Integer> ranksUsed = new ArrayList<>(); // Used to set the ranks for each card using random ranks
        HashSet<Character> suits = new HashSet<>(); // Used to have 3 unique suits
        ArrayList<Character> suitsUsed = new ArrayList<>(); // Used to set the suits for cards using random suits

        for(int i = 0; i < 25; i++) {
            // Generate 2 numbers from 2-14
            while (ranks.size() < 2) {
                int rand = (int) (Math.random() * 13 + 2);
                if (!ranks.contains(rand)) {
                    ranks.add(rand);
                    ranksUsed.add(rand);
                }
            }
            // Generate 3 random suits
            while (suits.size() < 3) {
                int rand = (int) (Math.random() * 4 + 1);

                if (!suits.contains('H') && rand == 1) {
                    suits.add('H');
                    suitsUsed.add('H');
                } else if (!suits.contains('C') && rand == 2) {
                    suits.add('C');
                    suitsUsed.add('C');
                } else if (!suits.contains('D') && rand == 3) {
                    suits.add('D');
                    suitsUsed.add('D');
                } else if (!suits.contains('S') && rand == 4) {
                    suits.add('S');
                    suitsUsed.add('S');
                }
            }
            // Use the 2 generated ranks
            c1.setRank(ranksUsed.get(1)); // First card for the pair
            c2.setRank(ranksUsed.get(0));
            c3.setRank(ranksUsed.get(1)); // Second card for the pair
            // Use generated suits
            c1.setSuit(suitsUsed.get(0));
            c2.setSuit(suitsUsed.get(1));
            c3.setSuit(suitsUsed.get(2));
            deck.sortHand(hand);
            assertEquals("PAIR", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a PAIR but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Clear the integers used
            ranks.clear();
            ranksUsed.clear();
            suits.clear();
            suitsUsed.clear();
        }
    }

    @Test
    void evalHandThreeTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 4, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        HashSet<Integer> ranks = new HashSet<>(); // Used to have 1 unique number
        ArrayList<Integer> ranksUsed = new ArrayList<>(); // Used to set the ranks for each card using random ranks
        HashSet<Character> suits = new HashSet<>(); // Used to have 3 unique suits
        ArrayList<Character> suitsUsed = new ArrayList<>(); // Used to set the suits for cards using random suits

        for(int i = 0; i < 25; i++) {
            // Generate 1 number from 2-14
            int randRank = (int) (Math.random() * 13 + 2);
            ranks.add(randRank);
            ranksUsed.add(randRank);

            // Generate 3 random suits
            while (suits.size() < 3) {
                int rand = (int) (Math.random() * 4 + 1);

                if (!suits.contains('H') && rand == 1) {
                    suits.add('H');
                    suitsUsed.add('H');
                } else if (!suits.contains('C') && rand == 2) {
                    suits.add('C');
                    suitsUsed.add('C');
                } else if (!suits.contains('D') && rand == 3) {
                    suits.add('D');
                    suitsUsed.add('D');
                } else if (!suits.contains('S') && rand == 4) {
                    suits.add('S');
                    suitsUsed.add('S');
                }
            }
            // Use the generated rank
            c1.setRank(ranksUsed.get(0));
            c2.setRank(ranksUsed.get(0));
            c3.setRank(ranksUsed.get(0));
            // Use generated suits
            c1.setSuit(suitsUsed.get(0));
            c2.setSuit(suitsUsed.get(1));
            c3.setSuit(suitsUsed.get(2));
            deck.sortHand(hand);
            assertEquals("THREE", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a THREE (of a kind) but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Clear the integers used
            ranks.clear();
            ranksUsed.clear();
            suits.clear();
            suitsUsed.clear();
        }
    }

    @Test
    void evalHandFlushTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 4, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        HashSet<Integer> ranks = new HashSet<>(); // Used to have 3 unique numbers
        ArrayList<Integer> ranksUsed = new ArrayList<>(); // Used to set the ranks for each card using random ranks
        HashSet<Character> suits = new HashSet<>(); // Used to have 1 unique suit
        ArrayList<Character> suitsUsed = new ArrayList<>(); // Used to set the suits for cards using random suits

        for(int i = 0; i < 25; i++) {
            // Generate 3 numbers from 2-14
            while (ranks.size() < 3) {
                int rand = (int) (Math.random() * 13 + 2);
                // We should make sure the generated numbers are not next to each other because we don't want to make straight flushes
                if(!ranks.contains(rand) && !ranksUsed.contains(rand + 1) && !ranksUsed.contains(rand - 1)) {
                    ranks.add(rand);
                    ranksUsed.add(rand);
                }
            }
            // Generate 1 suit
            int rand = (int) (Math.random() * 4 + 1);
            if(rand == 1) {
                suits.add('H');
                suitsUsed.add('H');
            } else if(rand == 2) {
                suits.add('C');
                suitsUsed.add('C');
            } else if (rand == 3) {
                suits.add('D');
                suitsUsed.add('D');
            } else if (rand == 4) {
                suits.add('S');
                suitsUsed.add('S');
            }

            // Use the generated rank
            c1.setRank(ranksUsed.get(0));
            c2.setRank(ranksUsed.get(1));
            c3.setRank(ranksUsed.get(2));
            // Use generated suit
            c1.setSuit(suitsUsed.get(0));
            c2.setSuit(suitsUsed.get(0));
            c3.setSuit(suitsUsed.get(0));
            deck.sortHand(hand);
            assertEquals("FLUSH", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a FLUSH but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Clear the integers used
            ranks.clear();
            ranksUsed.clear();
            suits.clear();
            suitsUsed.clear();
        }
    }

    @Test
    void evalHandStraightTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 4, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        HashSet<Integer> ranks = new HashSet<>(); // Used to have 1 unique number
        ArrayList<Integer> ranksUsed = new ArrayList<>(); // Used to set the ranks for each card using random ranks
        HashSet<Character> suits = new HashSet<>(); // Used to have 3 unique suits
        ArrayList<Character> suitsUsed = new ArrayList<>(); // Used to set the suits for cards using random suits

        for(int i = 0; i < 25; i++) {
            // Generate 1 number from 4-14
            int randRank = (int) (Math.random() * 11 + 4);
            ranks.add(randRank);
            ranksUsed.add(randRank);

            // Generate 3 random suits
            while (suits.size() < 3) {
                int rand = (int) (Math.random() * 4 + 1);

                if (!suits.contains('H') && rand == 1) {
                    suits.add('H');
                    suitsUsed.add('H');
                } else if (!suits.contains('C') && rand == 2) {
                    suits.add('C');
                    suitsUsed.add('C');
                } else if (!suits.contains('D') && rand == 3) {
                    suits.add('D');
                    suitsUsed.add('D');
                } else if (!suits.contains('S') && rand == 4) {
                    suits.add('S');
                    suitsUsed.add('S');
                }
            }
            // Use the generated rank
            c1.setRank(ranksUsed.get(0));
            c2.setRank(ranksUsed.get(0) - 1);
            c3.setRank(ranksUsed.get(0) - 2);
            // Use generated suits
            c1.setSuit(suitsUsed.get(0));
            c2.setSuit(suitsUsed.get(1));
            c3.setSuit(suitsUsed.get(2));
            deck.sortHand(hand);
            assertEquals("STRAIGHT", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a STRAIGHT but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Clear the integers used
            ranks.clear();
            ranksUsed.clear();
            suits.clear();
            suitsUsed.clear();
        }
    }

    @Test
    void evalHandStraightFlushTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 4, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        HashSet<Integer> ranks = new HashSet<>(); // Used to have 1 unique number
        ArrayList<Integer> ranksUsed = new ArrayList<>(); // Used to set the ranks for each card using random ranks
        HashSet<Character> suits = new HashSet<>(); // Used to have 1 unique suit
        ArrayList<Character> suitsUsed = new ArrayList<>(); // Used to set the suits for cards using random suits

        for(int i = 0; i < 25; i++) {
            // Generate 1 number from 4-14
            int randRank = (int) (Math.random() * 11 + 4);
            ranks.add(randRank);
            ranksUsed.add(randRank);
            // Generate 1 suit
            int rand = (int) (Math.random() * 4 + 1);
            if(rand == 1) {
                suits.add('H');
                suitsUsed.add('H');
            } else if(rand == 2) {
                suits.add('C');
                suitsUsed.add('C');
            } else if (rand == 3) {
                suits.add('D');
                suitsUsed.add('D');
            } else if (rand == 4) {
                suits.add('S');
                suitsUsed.add('S');
            }
            // Use the generated rank
            c1.setRank(ranksUsed.get(0));
            c2.setRank(ranksUsed.get(0) - 1);
            c3.setRank(ranksUsed.get(0) - 2);
            // Use generated suit
            c1.setSuit(suitsUsed.get(0));
            c2.setSuit(suitsUsed.get(0));
            c3.setSuit(suitsUsed.get(0));
            deck.sortHand(hand);
            assertEquals("STRAIGHT_FLUSH", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a STRAIGHT_FLUSH but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Clear the integers used
            ranks.clear();
            ranksUsed.clear();
            suits.clear();
            suitsUsed.clear();
        }
    }

    @Test
    void evalHandAceLowStraightsTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 4, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        HashSet<Character> suits = new HashSet<>(); // Used to have 1 unique suit
        ArrayList<Character> suitsUsed = new ArrayList<>(); // Used to set the suits for cards using random suits

        for(int i = 0; i < 25; i++) {
            // Generate 3 random suits
            while (suits.size() < 3) {
                int rand = (int) (Math.random() * 4 + 1);
                if (!suits.contains('H') && rand == 1) {
                    suits.add('H');
                    suitsUsed.add('H');
                } else if (!suits.contains('C') && rand == 2) {
                    suits.add('C');
                    suitsUsed.add('C');
                } else if (!suits.contains('D') && rand == 3) {
                    suits.add('D');
                    suitsUsed.add('D');
                } else if (!suits.contains('S') && rand == 4) {
                    suits.add('S');
                    suitsUsed.add('S');
                }
            }
            // Set hand to equal an Ace-Low Straight
            c1.setRank(14);
            c2.setRank(2);
            c3.setRank(3);
            // Use generated suits
            c1.setSuit(suitsUsed.get(0));
            c2.setSuit(suitsUsed.get(1));
            c3.setSuit(suitsUsed.get(2));
            deck.sortHand(hand);
            assertEquals("STRAIGHT", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a STRAIGHT but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Turn the straight into a Straight Flush
            c2.setSuit(suitsUsed.get(0));
            c3.setSuit(suitsUsed.get(0));
            assertEquals("STRAIGHT_FLUSH", ThreeCardLogic.evalHand(hand), "Hand " + i + " should be a STRAIGHT_FLUSH but isn't. It has rank and suit: " + c1.getRank() + " " + c1.getSuit() + " , " + c2.getRank() + " " + c2.getSuit() + ", " + c3.getRank() + " " + c3.getSuit());
            // Clear the chars used
            suits.clear();
            suitsUsed.clear();
        }
    }

    @Test
    void compareHighCardAndPairTest() {
        ArrayList<Card> player = new ArrayList<>(); // King High Card
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 13, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('C', 2, "");
        Card c5 = new Card('S', 3, "");
        Card c6 = new Card('D', 3, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(player), "Player hand is not a High Card");
        assertEquals("PAIR", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Pair");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player King High Card beat a Pair, incorrect result");
    }

    @Test
    void compareHighCardAndFlushTest() {
        ArrayList<Card> player = new ArrayList<>(); // King High Card
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 13, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('C', 2, "");
        Card c5 = new Card('C', 3, "");
        Card c6 = new Card('C', 8, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(player), "Player hand is not a High Card");
        assertEquals("FLUSH", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Flush");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player King High Card beat a Flush, incorrect result");
    }

    @Test
    void compareHighCardAndStraightTest() {
        ArrayList<Card> player = new ArrayList<>(); // King High Card
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 13, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('C', 2, "");
        Card c5 = new Card('D', 3, "");
        Card c6 = new Card('H', 4, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(player), "Player hand is not a High Card");
        assertEquals("STRAIGHT", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Straight");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player King High Card beat a Straight, incorrect result");
    }

    @Test
    void compareHighCardAndThreeTest() {
        ArrayList<Card> player = new ArrayList<>(); // King High Card
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 13, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('C', 8, "");
        Card c5 = new Card('S', 8, "");
        Card c6 = new Card('H', 8, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(player), "Player hand is not a High Card");
        assertEquals("THREE", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Three of a Kind");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player King High Card beat a Three of a Kind, incorrect result");
    }

    @Test
    void compareHighCardAndStraightFlushTest() {
        ArrayList<Card> player = new ArrayList<>(); // King High Card
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 13, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('C', 3, "");
        Card c5 = new Card('C', 4, "");
        Card c6 = new Card('C', 5, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(player), "Player hand is not a High Card");
        assertEquals("STRAIGHT_FLUSH", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Straight Flush");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player King High Card beat a Straight Flush, incorrect result");
    }

    @Test
    void comparePairAndHighCardTest() {
        ArrayList<Card> player = new ArrayList<>();
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 6, "");
        Card c3 = new Card('D', 6, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('S', 5, "");
        Card c5 = new Card('D', 9, "");
        Card c6 = new Card('C', 12, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("PAIR", ThreeCardLogic.evalHand(player), "Player hand is not a Pair");
        assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a High Card");
        assertEquals('W', ThreeCardLogic.compareHands(player, dealer), "A Player Pair did not beat a Queen High Card, incorrect result");
    }

    @Test
    void comparePairAndFlushTest() {
        ArrayList<Card> player = new ArrayList<>();
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 6, "");
        Card c3 = new Card('D', 6, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('S', 5, "");
        Card c5 = new Card('S', 9, "");
        Card c6 = new Card('S', 12, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("PAIR", ThreeCardLogic.evalHand(player), "Player hand is not a Pair");
        assertEquals("FLUSH", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Flush");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player Pair beat a Flush, incorrect result");
    }

    @Test
    void comparePairAndStraightTest() {
        ArrayList<Card> player = new ArrayList<>();
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 6, "");
        Card c3 = new Card('D', 6, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('S', 10, "");
        Card c5 = new Card('D', 11, "");
        Card c6 = new Card('C', 12, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("PAIR", ThreeCardLogic.evalHand(player), "Player hand is not a Pair");
        assertEquals("STRAIGHT", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Straight");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player Pair beat a Straight, incorrect result");
    }

    @Test
    void comparePairAndThreeTest() {
        ArrayList<Card> player = new ArrayList<>();
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 6, "");
        Card c3 = new Card('D', 6, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('S', 10, "");
        Card c5 = new Card('D', 10, "");
        Card c6 = new Card('C', 10, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("PAIR", ThreeCardLogic.evalHand(player), "Player hand is not a Pair");
        assertEquals("THREE", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Three of a Kind");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player Pair beat a Three of a Kind, incorrect result");
    }

    @Test
    void comparePairAndStraightFlushTest() {
        ArrayList<Card> player = new ArrayList<>();
        // Create 3 cards for a player
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 6, "");
        Card c3 = new Card('D', 6, "");

        ArrayList<Card> dealer = new ArrayList<>();
        // Create 3 cards for a dealer
        Card c4 = new Card('H', 10, "");
        Card c5 = new Card('H', 11, "");
        Card c6 = new Card('H', 12, "");

        // Add all cards to each hand
        player.add(c1);
        player.add(c2);
        player.add(c3);
        dealer.add(c4);
        dealer.add(c5);
        dealer.add(c6);

        assertEquals("PAIR", ThreeCardLogic.evalHand(player), "Player hand is not a Pair");
        assertEquals("STRAIGHT_FLUSH", ThreeCardLogic.evalHand(dealer), "Dealer hand is not a Straight Flush");
        assertEquals('L', ThreeCardLogic.compareHands(player, dealer), "A Player Pair beat a Straight Flush, incorrect result");
    }

    @Test
    void pushAnteTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 5, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);

        int num = 5;
        while(num < 12) {
            c3.setRank(num);
            assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(hand), "Player hand is not a High Card");
            assertTrue(ThreeCardLogic.pushAnte(hand));
            num++;
        }
    }

    @Test
    void evalPPWinningsHighCardTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 5, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);

        int num = 5;
        while(num < 14) {
            c3.setRank(num);
            assertEquals("HIGH_CARD", ThreeCardLogic.evalHand(hand), "Player hand is not a High Card");
            assertEquals(0, ThreeCardLogic.evalPPWinnings(hand, 10), "A High Card hand should earn 0 for Pair Plus");
            num++;
        }
    }

    @Test
    void evalPPWinningsPairTest() {
        // Create 3 cards
        Card c1 = new Card('H', 2, "");
        Card c2 = new Card('S', 3, "");
        Card c3 = new Card('D', 3, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);

        int num = 3;
        while(num < 15) {
            c2.setRank(num);
            c3.setRank(num);
            assertEquals("PAIR", ThreeCardLogic.evalHand(hand), "Player hand is not a Pair");
            assertEquals(10, ThreeCardLogic.evalPPWinnings(hand, 10), "A Pair hand should earn equal to its bet for Pair Plus");
            num++;
        }
    }

    @Test
    void evalPPWinningsFlushTest() {
        // Create 3 cards
        Card c1 = new Card('H', 4, "");
        Card c2 = new Card('H', 5, "");
        Card c3 = new Card('H', 7, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);

        int num = 0;
        while(num < 4) {
            c1.setRank(c1.getRank() + 1);
            c2.setRank(c2.getRank() + 1);
            c3.setRank(c3.getRank() + 1);
            assertEquals("FLUSH", ThreeCardLogic.evalHand(hand), "Player hand is not a Flush");
            assertEquals(30, ThreeCardLogic.evalPPWinnings(hand, 10), "A Flush hand should earn 3x to its bet for Pair Plus");
            num++;
        }
    }

    @Test
    void evalPPWinningsStraightTest() {
        // Create 3 cards
        Card c1 = new Card('H', 4, "");
        Card c2 = new Card('C', 5, "");
        Card c3 = new Card('S', 6, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);

        int num = 0;
        while(num < 4) {
            c1.setRank(c1.getRank() + 1);
            c2.setRank(c2.getRank() + 1);
            c3.setRank(c3.getRank() + 1);
            assertEquals("STRAIGHT", ThreeCardLogic.evalHand(hand), "Player hand is not a Straight");
            assertEquals(60, ThreeCardLogic.evalPPWinnings(hand, 10), "A Straight hand should earn 6x to its bet for Pair Plus");
            num++;
        }
    }

    @Test
    void evalPPWinningsThreeTest() {
        // Create 3 cards
        Card c1 = new Card('H', 5, "");
        Card c2 = new Card('S', 5, "");
        Card c3 = new Card('D', 5, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);

        int num = 0;
        while(num < 8) {
            c1.setRank(c1.getRank() + 1);
            c2.setRank(c2.getRank() + 1);
            c3.setRank(c3.getRank() + 1);
            assertEquals("THREE", ThreeCardLogic.evalHand(hand), "Player hand is not a Three of a Kind");
            assertEquals(300, ThreeCardLogic.evalPPWinnings(hand, 10), "A Flush hand should earn 30x to its bet for Pair Plus");
            num++;
        }
    }

    @Test
    void evalPPWinningsStraightFlushTest() {
        // Create 3 cards
        Card c1 = new Card('H', 4, "");
        Card c2 = new Card('H', 5, "");
        Card c3 = new Card('H', 6, "");
        // Add all cards to the hand
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);

        int num = 0;
        while(num < 4) {
            c1.setRank(c1.getRank() + 1);
            c2.setRank(c2.getRank() + 1);
            c3.setRank(c3.getRank() + 1);
            assertEquals("STRAIGHT_FLUSH", ThreeCardLogic.evalHand(hand), "Player hand is not a Straight Flush");
            assertEquals(400, ThreeCardLogic.evalPPWinnings(hand, 10), "A Flush hand should earn 40x to its bet for Pair Plus");
            num++;
        }
    }
}
