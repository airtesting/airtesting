package mujava.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGui {
	private JFrame janela;
	JPanel painelPrincipal;

	private void preparaJanela() {
		janela = new JFrame("MUJAVA");
		//janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void mostraJanela() {
		janela.pack();
		janela.setSize(300, 250);
		janela.setVisible(true);
	}

	private void preparaPainelPrincipal() {
		painelPrincipal = new JPanel();
		janela.add(painelPrincipal);
	}

	private void preparaBotaoCarregar() {
		JButton botaoCarregar = new JButton("Gen Mutantas Main");
		botaoCarregar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					GenMutantsMain.MutantsMain();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		painelPrincipal.add(botaoCarregar);
	}

	private void preparaBotaoSair() {
		JButton botaoSair = new JButton("Run Test Main");
		botaoSair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RunTestMain.TestMain();
			}
		});
		painelPrincipal.add(botaoSair);
	}

	public void montaTela() {
		preparaJanela();
		preparaPainelPrincipal();
		preparaBotaoCarregar();
		preparaBotaoSair();
		mostraJanela();
	}

	public static void main(String args[]) {
		new MainGui().montaTela();
	}

}