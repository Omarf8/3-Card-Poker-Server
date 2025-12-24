import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Deck {
    public ArrayList<Card> deck;

    public Deck() {
        this.deck = new ArrayList<>();
        createDeck();
    }

    public void createDeck() {
        char s = 'H'; // Suit is initially Hearts
        int r = 2; // Start at rank 2

        for(int i = 1; i < 53; i++) {
            this.deck.add(new Card(s, r, "8BitDeck" + i + ".png"));
            r++; // Increment the rank

            if(r > 14) {
                r = 2; // Reset
                if(s == 'H') {
                    s = 'C';
                }
                else if(s == 'C') {
                    s = 'D';
                }
                else if(s == 'D') {
                    s = 'S';
                }
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.deck);
    }

    public void sortHand(ArrayList<Card> hand) {
        // When sorting, I want it to be Hearts then Clubs then Diamonds then Spades if the ranks are equal
        Map<Character, Integer> suitOrder = Map.of(
                'H', 1,
                'C', 2,
                'D', 3,
                'S', 4
        );

        hand.sort((card1, card2) -> {
            if(card1.getRank() < card2.getRank()) {
                return -1;
            }
            else if(card1.getRank() > card2.getRank()) {
                return 1;
            }
            else {
                // Compare suits when ranks are equal
                int suit1 = suitOrder.get(card1.getSuit());
                int suit2 = suitOrder.get(card2.getSuit());

                if(suit1 < suit2) {
                    return -1;
                }
                else if(suit1 > suit2) {
                    return 1;
                }
                else {
                    return 0;
                }
            }
        });
    }

    public ArrayList<Card> deal3Cards() {
        ArrayList<Card> hand = new ArrayList<>();

        while(hand.size() < 3) {
            hand.add(this.deck.remove(deck.size() - 1));
        }

        sortHand(hand); // Sort the hand before returning it
        return hand;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }
}
