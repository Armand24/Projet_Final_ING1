package Projet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Random;

public class NoisingGUI extends JFrame {

    private JLabel originalLabel;
    private JLabel noisyLabel;
    private BufferedImage originalImage;
    private BufferedImage noisyImage;
    private JSlider sigmaSlider;
    private JButton saveButton;

    public NoisingGUI() {
        setTitle("Débruitage d'image par bruit gaussien");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top Panel: Image selection
        JPanel topPanel = new JPanel();
        JButton loadButton = new JButton("Charger une image");
        loadButton.addActionListener(e -> loadImage());
        topPanel.add(loadButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Images
        JPanel imagePanel = new JPanel(new GridLayout(1, 2));
        originalLabel = new JLabel("Image d'origine", JLabel.CENTER);
        noisyLabel = new JLabel("Image bruitée", JLabel.CENTER);
        imagePanel.add(originalLabel);
        imagePanel.add(noisyLabel);
        add(imagePanel, BorderLayout.CENTER);

        // Bottom Panel: Slider + Save
        JPanel bottomPanel = new JPanel();
        sigmaSlider = new JSlider(0, 50, 10);
        sigmaSlider.setMajorTickSpacing(10);
        sigmaSlider.setMinorTickSpacing(5);
        sigmaSlider.setPaintTicks(true);
        sigmaSlider.setPaintLabels(true);
        sigmaSlider.setBorder(BorderFactory.createTitledBorder("σ (Écart-type du bruit)"));

        sigmaSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (originalImage != null) {
                    applyNoise(sigmaSlider.getValue());
                }
            }
        });

        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> saveImage());
        saveButton.setEnabled(false);

        bottomPanel.add(sigmaSlider);
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                originalImage = ImageIO.read(file);
                ImageIcon icon = new ImageIcon(originalImage.getScaledInstance(400, -1, Image.SCALE_SMOOTH));
                originalLabel.setIcon(icon);
                applyNoise(sigmaSlider.getValue());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors du chargement : " + ex.getMessage());
            }
        }
    }

    private void applyNoise(double sigma) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        noisyImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Random rand = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = new Color(originalImage.getRGB(x, y)).getRed();
                int noisyPixel = (int) (pixel + rand.nextGaussian() * sigma);
                noisyPixel = Math.max(0, Math.min(255, noisyPixel));
                Color gray = new Color(noisyPixel, noisyPixel, noisyPixel);
                noisyImage.setRGB(x, y, gray.getRGB());
            }
        }

        ImageIcon icon = new ImageIcon(noisyImage.getScaledInstance(400, -1, Image.SCALE_SMOOTH));
        noisyLabel.setIcon(icon);
        saveButton.setEnabled(true);
    }

    private void saveImage() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                ImageIO.write(noisyImage, "png", file);
                JOptionPane.showMessageDialog(this, "Image enregistrée !");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NoisingGUI::new);
    }
}
