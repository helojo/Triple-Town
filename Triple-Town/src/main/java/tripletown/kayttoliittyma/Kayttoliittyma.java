package tripletown.kayttoliittyma;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import tripletown.sovellus.Peli;
import tripletown.sovellus.Pistetilasto;

/**
 * Luokka piirtää pelin graafisen käyttöliittymän sekä päivittää pelitilanteen.
 */
public class Kayttoliittyma implements Runnable {

    private JFrame frame;
    private JPanel aloitusPaneeli;
    private JLabel seuraavaPala;
    private JLabel pisteet;
    private final Peli peli;
    private final int leveys = 6;
    private final int korkeus = 6;
    private final JButton[][] ruudut;
    private int arvottuPala;
    private Pistetilasto tilasto;
    private final String[] kuvat = {
        "src/main/resources/kuvat/karhu.png",
        "src/main/resources/kuvat/ruoho.png",
        "src/main/resources/kuvat/pensas.png",
        "src/main/resources/kuvat/puu.png",
        "src/main/resources/kuvat/talo.png",
        "src/main/resources/kuvat/kartano.png",
        "src/main/resources/kuvat/linna.png"};
    private final String[] palat = {"", "ruoho", "pensas", "puu", "talo", "kartano", "linna"};

    public Kayttoliittyma() {
        peli = new Peli(leveys, korkeus);
        ruudut = new JButton[leveys][korkeus];
        tilasto = new Pistetilasto();
    }

    @Override
    public void run() {
        frame = new JFrame("Triple Town");
        frame.setPreferredSize(new Dimension(400, 420));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(new GridLayout(leveys, korkeus));

        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.pack();

        luoAloitusRuutu();
//        lisaaRuudukko();
        frame.setVisible(true);
//
//        peli.alustaPelilauta();
//        paivita();

    }

    public JFrame getFrame() {
        return frame;
    }

    public void aloitaPeli() {
        JPanel ylaPalkki = new JPanel();
        pisteet = new JLabel("Pisteet: " + peli.pistetilanne());
        
        seuraavaPala = new JLabel("  Aseta " + getArvottuPala());
        JButton lopetaNappi = new JButton("Lopeta");
        lopetaNappi.addActionListener(new LopetaPeliKuuntelija());

        ylaPalkki.add(pisteet);
        ylaPalkki.add(seuraavaPala);
        ylaPalkki.add(lopetaNappi);

        JPanel peliruudukko = luoRuudukko();

        frame.remove(aloitusPaneeli);
        
        frame.add(ylaPalkki, BorderLayout.NORTH);
        frame.add(peliruudukko);
        peli.alustaPelilauta();
        paivita();
    }

    /**
     * Metodi luo peliruudut ja lisää ne frameen.
     */
    private JPanel luoRuudukko() {

        JPanel ruudukko = new JPanel(new GridLayout(leveys, korkeus));

        for (int y = 0; y < korkeus; y++) {
            for (int x = 0; x < leveys; x++) {

                JButton ruutu = new JButton();

                ruutu.addActionListener(new PeliKuuntelija(peli, this, x, y));
                ruutu.setBackground(Color.green);
                ruudut[x][y] = ruutu;

                ruudukko.add(ruutu);
            }
        }

        return ruudukko;
    }

    /**
     * Metodi päivittää pelitilanteen ajan tasalle.
     *
     */
    public void paivita() {

        pisteet.setText("Pisteet: " + peli.pistetilanne());
        
        peli.liikutaKarhua();

        for (int y = 0; y < korkeus; y++) {
            for (int x = 0; x < leveys; x++) {

                asetaRuudunSisalto(x, y);
            }
        }

        if (peli.pelilautaTaynna()) {
            tallennaPisteet();

        } else {
            this.arvottuPala = peli.arvoPala();
            seuraavaPala.setText(" Aseta " + palat[arvottuPala]);
        }
    }

    /**
     * Metodi tulostaa ruutuun sen sisällön.
     *
     * @param x Ruudun x-koordinaatti
     * @param y Ruudun y-koordinaatti
     */
    private void asetaRuudunSisalto(int x, int y) {

        if (peli.getRuutu(x, y) != null) {
            int palanumero = peli.getRuutu(x, y).getId();

            ruudut[x][y].setIcon(new ImageIcon(kuvat[palanumero]));
        } else {
            ruudut[x][y].setIcon(null);
        }

    }

    /**
     * Metodi palauttaa Palan, joka on sillä hetkellä asetettavana.
     *
     * @return Palauttaa asetettavan Palan palanumeron.
     */
    public int getArvottuPala() {
        return this.arvottuPala;
    }

    /**
     * Metodi hakee pelin lopussa kertyneet pisteet ja lähettää käskyn tallettaa
     * pisteet.
     */
    private void tallennaPisteet() {
        tilasto = new Pistetilasto();
        tilasto.tallennaPisteet(peli.pistetilanne());
    }

    private void luoAloitusRuutu() {
        aloitusPaneeli = new JPanel();
        JButton pelaaNappi = new JButton("Pelaa");
        JButton pisteetNappi = new JButton("Pistetilasto");
        JButton lopetaNappi = new JButton("Lopeta");

        pelaaNappi.addActionListener(new AloitaPeliKuuntelija(this));
        pisteetNappi.addActionListener(new PistetilastonKuuntelija());
        lopetaNappi.addActionListener(new LopetaPeliKuuntelija());

        aloitusPaneeli.add(pelaaNappi);
        aloitusPaneeli.add(pisteetNappi);
        aloitusPaneeli.add(lopetaNappi);
        frame.add(aloitusPaneeli);
    }

}