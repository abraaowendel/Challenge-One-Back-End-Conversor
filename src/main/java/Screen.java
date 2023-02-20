import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Screen extends JFrame {
    public Screen() throws Exception {

        setTitle("Conversor");
        setVisible(true);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.DARK_GRAY);

        try{

            double valorOriginal = Double.parseDouble(JOptionPane.showInputDialog("Insira um valor"));

            if(valorOriginal > 0.0){
                String[] moedas = {"BRL", "USD", "EUR", "GBP", "JPY", "RUB"};

                String moedaSelecionada1 = selecionarMoeda(moedas, "Selecione uma Opção: ");

                String[] novasMoedas = obterNovasMoedas(moedas, moedaSelecionada1);

                String moedaSelecionada2 = selecionarMoeda(novasMoedas, "Selecione uma Opção: ");

                if (!moedaSelecionada1.equals(moedaSelecionada2)) {
                    JDialog dialog = exibirAviso("Calculando conversão, aguarde...");

                    double moeda = pegarMoedas(moedaSelecionada1, moedaSelecionada2);
                    double valorConvertido = valorOriginal * moeda;

                    dialog.dispose();

                    JOptionPane.showMessageDialog(null, "RESULTADO: " + valorConvertido, "VALOR CONVERTIDO", JOptionPane.PLAIN_MESSAGE);
                }
            }
            else{
                throw new IllegalArgumentException();
            }

        }
        catch (NumberFormatException e){
            JOptionPane.showMessageDialog(null,"SÃO ACEITOS APENAS NÚMEROS","ERRO", JOptionPane.ERROR_MESSAGE);
        }
        catch (IllegalArgumentException e){
            JOptionPane.showMessageDialog(null, "VALOR A SER CONVERTIDO TEM QUE SER MAIOR QUE 0", "ERRO", JOptionPane.ERROR_MESSAGE);
        }
        finally {
            System.exit(0);
        }
    }

    private String selecionarMoeda(String[] moedas, String mensagem) {
        JPanel painelMoeda= new JPanel();
        JComboBox<String> comboBox = new JComboBox<>(moedas);
        painelMoeda.add(comboBox);

        int resultadoOrigem = JOptionPane.showOptionDialog(null, painelMoeda,
                mensagem, JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, null, null);

        String moedaSelecionada = null;
        if (resultadoOrigem == JOptionPane.OK_OPTION) {
            moedaSelecionada = (String) comboBox.getSelectedItem();
        }

        return moedaSelecionada;
    }

    private String[] obterNovasMoedas(String[] moedas, String moedaSelecionada1) {
        String[] novasMoedas = new String[moedas.length - 1];
        int j = 0;
        for (String moeda : moedas) {
            if (!moeda.equals(moedaSelecionada1)) {
                novasMoedas[j] = moeda;
                j++;
            }
        }
        return novasMoedas;
    }

    private JDialog exibirAviso(String mensagem) {
        JDialog dialog = new JDialog();
        JOptionPane optionPane = new JOptionPane(mensagem, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        dialog.setLocationRelativeTo(null);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }
    public double pegarMoedas(String moeda1, String moeda2) throws Exception {
        String urlParaChamada = "https://economia.awesomeapi.com.br/json/last/" + moeda1 + "-" + moeda2;

        try {
            URL url = new URL(urlParaChamada);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");

            if (conexao.getResponseCode() != 200) {
                throw new RuntimeException("HTTP error code : " + conexao.getResponseCode());
            }

            BufferedReader resposta = new BufferedReader(new InputStreamReader(conexao.getInputStream()));

            Gson gson = new Gson();

            JsonElement jsonElement = JsonParser.parseReader(resposta);
            JsonObject rootObject = ((JsonElement) jsonElement).getAsJsonObject();
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String myKey = jsonObject.keySet().toString().replace("[", "").replace("]", "");
            JsonObject jsonRootObject = rootObject.getAsJsonObject(myKey);
            String high = jsonRootObject.get("high").getAsString();

            return Double.parseDouble(high);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void mostrarDialogoAguardando() {
        JDialog dialog = new JDialog();
        JOptionPane optionPane = new JOptionPane("Carregando moedas, aguarde...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        dialog.setLocationRelativeTo(null);
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.pack();
        dialog.setVisible(true);
    }

    public void fecharDialogoAguardando() {
        Window window = SwingUtilities.getWindowAncestor(this);
        window.dispose();
    }

}
