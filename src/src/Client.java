import java.awt.Color;
import javax.swing.event.DocumentListener;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.*;
import javax.swing.event.*;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.net.*;
import java.awt.event.WindowEvent;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;



/**
 * @author DEROUAULT
 * @version 0.1 : Date : Jeudi 11 Fevrier
 * Classe permettante d'afficher et de gerer toutes la partie graphique et communication du client
 */
public class Client extends JFrame {

    /**
     * Represente le titre de la this
     */
    private String titre;


    //Connexion avec le serveur
    /**
     * Represente le port utilise pour la connexion
     */
    private int port;
    /**
     * Represente le nom de domaine utilise pour la connexion
     */
    private String nomDomaine;
    /**
     * Represente le pseudo utilise pour la connexion
     */
    private String pseudo;
    /**
     * Represente si l'on est connecte ou pas a un server
     */
    private boolean estConnecte;
    /**
     * Represente le socket du server
     */
    private Socket server;
    /**
     * Represente la variable de reception pour la communication avec le server
     */
    private BufferedReader inputServer;
    /**
     * Represente la variable d'envoie pour la communication avec le server
     */
    private PrintWriter outputServer;
    /**
     * Represente la class qui est en thread permettant d'ecouter si le serveur nous envoie un message
     */
    private Thread reception;



    //Panneau
    /**
     * Panel du haut qui conserve la connexion au server
     */
    final private JPanel connexion = new JPanel(new FlowLayout());
    /**
     * Panel general qui regroupe tout
     */
    final private JPanel page = new JPanel(new BorderLayout(25,25));

    /**
     * Panel qui regroupe le fil et envoie de message
     */
    final private JPanel contenu = new JPanel(new BorderLayout(25,25));

    /**
     * Panel qui regroupe la liste des connectes
     */
    final private JPanel connectes = new JPanel();
    /**
     * Panel qui groupe le fil de message
     */
    final private JPanel jpMessage = new JPanel();
    /**
     * Panel qui regroupe l'envoie de message
     */
    final private JPanel jpEnvoie = new JPanel();



    /**
     * Represente le champ permettant la saisie du pseudo
     */
    final private JTextField nom = new JTextField("", 8);
    /**
     * Represente le champ permettant la saisie du nom de domaine
     */
    final private JTextField jtfIp = new JTextField("localhost", 8);
    /**
     * Reprenste le champ permettant la saisie du port
     */
    final private JTextField jtfPort = new JTextField("5000", 8);
    /**
     * Represente le bouton pour se connecter
     */
    final private JButton seConnecter = new JButton("Connexion");

    /**
     * Représente le bouton information
     */
    final private JButton aide = new JButton("AIDE");

    //Panneau - connectes
    /**
     * Represent le label pour la liste de connectes
     */
    final private JLabel labelConnectes = new JLabel("Connectes :");
    /**
     * Represente le champ pour inserer la liste des connectes
     */
    final private JTextPane listeConnectes = new JTextPane();
    /**
     * Represente le style du document pour la liste de connectes
     */
    private StyledDocument dlisteConnectes;

    /**
     * Represente le scrollpane de la liste des connectes
     */
    final private JScrollPane scrollListCo = new JScrollPane(listeConnectes);

    //Panneau - fil de message
    /**
     * Represente le labal pour le fil de message
     */
    final private JLabel labelMessage = new JLabel("Discussion");



    /**
     * Represente le champ pour afficher la fil de message
     */
    final private JTextPane filMessage = new JTextPane();

    /**
     * Represente le scrollpane du fil de message
     */
    final private JScrollPane scrollFilMessage = new JScrollPane(filMessage);
    /**
     * Represente le style du document pour le fil de message
     */
    private StyledDocument dFilMessage;
    /**
     * Represente le style normal de la police pour le document
     */
    final private SimpleAttributeSet style_normal = new SimpleAttributeSet();
    /**
     * Represente le label pour l'envoie de message
     */
    final private JLabel envoieMessage = new JLabel("Message");
    /**
     * Represente le champ pour saisir un message
     */
    final private JTextArea envoie = new JTextArea(5, 50);

    /**
     * Represente le scrollpane de l'envoie de message
     */
    final private JScrollPane scrollEnvoie = new JScrollPane(envoie);

    /**
     * Represente le bouton pour envoyer un message
     */
    final private JButton envoieUnMessage  = new JButton("Envoyer");

    /**
     * Action quand il y a une connexion
     */
    final private ActionListener eventConnexion;
    /**
     * Action quand il y a un deconnexion
     */
    final private ActionListener eventDeconnexion;

    /**
     * Constructeur de la classe permettant d'initialiser et d'afficher le client
     */
    public Client() {
        //Definition de la fenêtre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        titre = "";
        estConnecte = false;
        this.setSize(800, 700);
        this.setLocationRelativeTo(null);

        //Event lorsque l'on clique sur le bouton aide
        aide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        //Event lorsque l'on clique sur le bouton connexion
        eventConnexion = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //On test si la connexion est OK
                if(tentativeConnexion()) {
                    //On rends disponible la possibilité d'envoyer un message
                    envoieUnMessage.setEnabled(true);
                    //On indique que l'utilisateurs est connecté
                    estConnecte = true;
                    setTitre();

                    //On rends indisponible le changement des infos de connexion
                    nom.setEditable(false);
                    jtfIp.setEditable(false);
                    jtfPort.setEditable(false);

                    envoie.setEditable(true);

                    //On change le bouton en déconnexion
                    seConnecter.setText("Deconnexion");
                    seConnecter.removeActionListener(eventConnexion);
                    seConnecter.addActionListener(eventDeconnexion);
                }
            }
        };

        //Event lorsque l'on clique sur le bouton deconnexion
        eventDeconnexion = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deconnexion();
            }
        };

        //Creation des 3 differentes panneaux
        //Connexion au server
        setPanneauConnexion();


        page.add(contenu,BorderLayout.CENTER);

        setTitre();


        this.setContentPane(page);
        this.setVisible(true);
    }

    //Methode permettant l'initialisation

    /**
     * Initialise en créant tout les éléments du panneau connexion
     */
    private void setPanneauConnexion() {
        DocumentListener peutSeConnecter = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void changed() {
                //On test si les trois champs sont remplis pour rendre accessible la connexion
                rendreAccessibleConnexion();
                setTitre();
            }

        };

        //Crée le label NOM
        JLabel labelNom = new JLabel("Nom :");
        nom.getDocument().addDocumentListener(peutSeConnecter);

        //Crée le label IP
        JLabel labelIp = new JLabel("IP :");

        //Défini la zone de saisie pour l'IP et met en place un hover
        jtfIp.setForeground(Color.GRAY);
        jtfIp.getDocument().addDocumentListener(peutSeConnecter);
        jtfIp.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jtfIp.getText().equals("localhost")) {
                    jtfIp.setText("");
                    jtfIp.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jtfIp.getText().isEmpty()) {
                    jtfIp.setText("localhost");
                    jtfIp.setForeground(Color.GRAY);
                }
            }
        });

        //Crée le label Port
        JLabel labelPort = new JLabel("Port :");

        //Défini la zone de saisie pour le port et met en place un hover
        jtfPort.setForeground(Color.GRAY);
        jtfPort.getDocument().addDocumentListener(peutSeConnecter);
        jtfPort.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jtfPort.getText().equals("5000")) {
                    jtfPort.setText("");
                    jtfPort.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jtfPort.getText().isEmpty()) {
                    jtfPort.setText("5000");
                    jtfPort.setForeground(Color.GRAY);
                }
            }
        });

        //Désactive par défaut le bouton connecter
        seConnecter.setEnabled(false);
        seConnecter.addActionListener(eventConnexion);

        //Ajoute les éléments au container connexion
        connexion.add(labelNom);
        connexion.add(nom);
        connexion.add(labelIp);
        connexion.add(jtfIp);
        connexion.add(labelPort);
        connexion.add(jtfPort);
        connexion.add(seConnecter);
        connexion.add(aide);

        page.add(connexion,BorderLayout.NORTH);
    }







    //Methode graphique

    /**
     * Test les 3 champs nom, ip et port afin de rendre disponible le bouton connexion si les 3 champs ne sont pas vides
     */
    public void rendreAccessibleConnexion() {
        //Si les 3 champs ne sont pas vide alors on rends le bouton connexion
        seConnecter.setEnabled(!(nom.getText().isEmpty()) && !(jtfIp.getText().isEmpty()) && !(jtfPort.getText().isEmpty()));

    }




    /**
     * Modifie le titre de la fenêtre
     */
    public void setTitre() {
        titre = nom.getText();
        this.setTitle("Client : " + titre + " - Connexion : " + estConnecte);
    }


    //Methode client - server

    /**
     * Tente de mettre en place une connexion client-server
     * @return boolean si la connexion est ok ou non
     */
    public boolean tentativeConnexion() {
        try{
            //Recuperation des informations
            pseudo = nom.getText();
            if(pseudo.length() > 20)
                pseudo = pseudo.substring(0,20);
            port = Integer.parseInt(jtfPort.getText());

            nomDomaine = jtfIp.getText();

            //Information que l'on va se connecter
            JOptionPane.showMessageDialog(this, "Tentative de connexion a " + nomDomaine + " sur le port " + port, "Se connecte", JOptionPane.INFORMATION_MESSAGE);

            //Connexion
            server = new Socket(nomDomaine, port);

            //Connecte
            JOptionPane.showMessageDialog(this, "Connecte sur le serveur " + nomDomaine, "Connecte", JOptionPane.INFORMATION_MESSAGE);

            //Initialisation de l'envoie et reception
            inputServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
            outputServer = new PrintWriter(server.getOutputStream(), true);

            //Envoie du pseudo
            outputServer.println("+" + pseudo);

            //On demarre le thread pour ecouter si l'on reçoit un message
            reception = new Reception();
            reception.start();

            return true;
        }catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Impossible de se connecter sur le serveur " + nomDomaine, "Erreur", JOptionPane.INFORMATION_MESSAGE);
        }
        return false;
    }

    /**
     * Traitement lors de la deconnexion
     */
    public void deconnexion(){
        //On eteinds le thread qui receptionne
        reception.interrupt();

        //On rends disponible le changement des infos de connexion
        nom.setEditable(true);
        jtfIp.setEditable(true);
        jtfPort.setEditable(true);
        //On rends indisponible les éléments de discussion

        //On indique à l'utilisateurs qu'il est déconnecté
        JOptionPane.showMessageDialog( this, "Deconnecte du serveur "+nomDomaine, "Connecte", JOptionPane.INFORMATION_MESSAGE);

        //On coupe la connexion sortante pour le server
        outputServer.println("-"+pseudo);
        outputServer.close();

        //On rends indisponible la possibilité d'envoyer un message
        envoieUnMessage.setEnabled(false);

        //On change le bouton en connexion
        seConnecter.setText("Connexion");
        seConnecter.setEnabled(true);
        seConnecter.removeActionListener(eventDeconnexion);
        seConnecter.addActionListener(eventConnexion);

        envoie.setEditable(false);

        //On efface le fil de message ainsi que la liste de connectes
        listeConnectes.setText(null);
        filMessage.setText(null);
        listeConnectes.setText(null);

        //On indique que l'utilisateurs est déconnecté
        estConnecte = false;
        setTitre();
    }

    /**
     * Permet d'envoyer un message au server
     */
    public void envoieCeMessage(String message){
        try {
            outputServer.println(message);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Envoie du message impossible sur le server " + nomDomaine, "Erreur", JOptionPane.INFORMATION_MESSAGE);
        }
    }



    /**
     * Lance le programme
     * @param args Non utilise ici
     */
    public static void main(String[] args){
        new Client();
    }

    public void pseudoPris(String pseudo){
        //On se déconnecte
        deconnexion();
        JOptionPane.showMessageDialog( this,"Pseudo pris", "Le pseudo est deja pris ! Veuillez le changer !", JOptionPane.INFORMATION_MESSAGE);
        nom.setText("");
    }

    /**
     * Classe permettante de gerer la reception de message
     */
    class Reception extends Thread{
        /**
         * Permet de gérer la reception de message du server
         */
        @Override
        public void run(){

        }
    }
}
