import java.util.ArrayList;
import java.util.Map;

public class ThreeCardLogic {
    public final static Map<String, Integer> ppTable = Map.of(
            "STRAIGHT_FLUSH", 40,
            "THREE", 30,
            "STRAIGHT", 6,
            "FLUSH", 3,
            "PAIR", 1
    );

    public static boolean pushAnte(ArrayList<Card> dealerHand) {
        return evalHand(dealerHand).equals("HIGH_CARD") && dealerHand.get(2).getRank() < 12;
    }

    public static String evalHand(ArrayList<Card> hand) {
        // We have to consider whether our straight is a low 2-3-A straight as well as a normal straight
        boolean straight = (hand.get(0).getRank() + 1 == hand.get(1).getRank() && hand.get(1).getRank() + 1 == hand.get(2).getRank()) ||
                (hand.get(0).getRank() == 2 && hand.get(1).getRank() == 3 && hand.get(2).getRank() == 14);

        boolean flush = (hand.get(0).getSuit() == hand.get(1).getSuit() && hand.get(1).getSuit() == hand.get(2).getSuit());

        boolean three = (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(1).getRank() == hand.get(2).getRank());

        boolean pair = (hand.get(0).getRank() == hand.get(1).getRank() || hand.get(0).getRank() == hand.get(2).getRank() || hand.get(1).getRank() == hand.get(2).getRank());

        if(straight && flush) { return "STRAIGHT_FLUSH"; }
        else if(three) { return "THREE"; }
        else if(straight) { return "STRAIGHT"; }
        else if(flush) { return "FLUSH"; }
        else if(pair) { return "PAIR"; }

        return "HIGH_CARD";

    }

    public static int evalPPWinnings(ArrayList<Card> hand, int bet) {
        String pokerHand = evalHand(hand);

        if(pokerHand.equals("HIGH_CARD")) {
            return 0; // Return 0 if the player did not get higher than a high card
        }

        return ppTable.get(pokerHand) * bet;
    }

    public static char compareHands(ArrayList<Card> player, ArrayList<Card> dealer) {
        Map<String, Integer> winOrder = Map.of(
                "STRAIGHT_FLUSH", 6,
                "THREE", 5,
                "STRAIGHT", 4,
                "FLUSH", 3,
                "PAIR", 2,
                "HIGH_CARD", 1
        );

        String playerPokerHand = evalHand(player);
        String dealerPokerHand = evalHand(dealer);

        // If the players hand is better than the dealer's hand return W (win)
        if(winOrder.get(playerPokerHand) > winOrder.get(dealerPokerHand)) {
            return 'W';
        }
        // If the players hand is worse than the dealer's hand return L (lose)
        else if(winOrder.get(playerPokerHand) < winOrder.get(dealerPokerHand)) {
            return 'L';
        }

        // Otherwise we should compare each card if the poker type is the same
        if(playerPokerHand.equals("STRAIGHT_FLUSH") || playerPokerHand.equals("STRAIGHT")) {
            boolean playerAceLow = (player.get(0).getRank() == 2 && player.get(1).getRank() == 3 && player.get(2).getRank() == 14);
            boolean dealerAceLow = (dealer.get(0).getRank() == 2 && dealer.get(1).getRank() == 3 && dealer.get(2).getRank() == 14);

            // The first two conditions check if either are an ace low hand, in which case the other person should win
            if(!playerAceLow && dealerAceLow) { return 'W'; }
            else if(playerAceLow && !dealerAceLow) { return 'L'; }
            // These conditions check the highest card for the straight since the rest of the cards would be the same rank
            else if(player.get(2).getRank() > dealer.get(2).getRank()) { return 'W'; }
            else if(player.get(2).getRank() < dealer.get(2).getRank()) { return 'L'; }
            // If we have the same rank straights then at the end of the function we would return 'P' for pushing the ante
        }
        else if(playerPokerHand.equals("THREE")) {
            // In hindsight, we don't need this comparison because only one player will have a three of a kind
            // If we have a higher rank then we win
            if(player.get(0).getRank() > dealer.get(0).getRank()) { return 'W'; }
            // Otherwise if it's lower than our rank then we lose
            else if(player.get(0).getRank() < dealer.get(0).getRank()) { return 'L'; }
        }
        else if(playerPokerHand.equals("FLUSH")) {
            // Check the highest cards
            if(player.get(2).getRank() > dealer.get(2).getRank()) { return 'W'; }
            else if(player.get(2).getRank() < dealer.get(2).getRank()) { return 'L'; }
            // Check the second highest cards
            else if(player.get(1).getRank() > dealer.get(1).getRank()) { return 'W'; }
            else if(player.get(1).getRank() < dealer.get(1).getRank()) { return 'L'; }
            // Check the third highest cards
            else if(player.get(0).getRank() > dealer.get(0).getRank()) { return 'W'; }
            else if(player.get(0).getRank() < dealer.get(0).getRank()) { return 'L'; }
        }
        else if(playerPokerHand.equals("PAIR")) {
            // The middle card is always one of the cards within the pair to compare
            if(player.get(1).getRank() > dealer.get(1).getRank()) { return 'W'; }
            else if(player.get(1).getRank() < dealer.get(1).getRank()) { return 'L'; }
            // We can use XOR to remove the duplicates (the pair) and have the last card remaining to compare
            int playerRank = player.get(0).getRank() ^ player.get(1).getRank() ^ player.get(2).getRank();
            int dealerRank = dealer.get(0).getRank() ^ dealer.get(1).getRank() ^ dealer.get(2).getRank();

            if(playerRank > dealerRank) { return 'W'; }
            else if(playerRank < dealerRank) { return 'L'; }
        }
        else if(playerPokerHand.equals("HIGH_CARD")) {
            // Check the highest cards
            if(player.get(2).getRank() > dealer.get(2).getRank()) { return 'W'; }
            else if(player.get(2).getRank() < dealer.get(2).getRank()) { return 'L'; }
            // Check the second highest cards
            else if(player.get(1).getRank() > dealer.get(1).getRank()) { return 'W'; }
            else if(player.get(1).getRank() < dealer.get(1).getRank()) { return 'L'; }
            // Check the third highest cards
            else if(player.get(0).getRank() > dealer.get(0).getRank()) { return 'W'; }
            else if(player.get(0).getRank() < dealer.get(0).getRank()) { return 'L'; }
        }

        return 'P'; // In the unlikely scenario where both hands are equal in strength, we should push the ante
    }
}
