package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.ConnectionFactory;
import br.com.alura.bytebank.domain.RegraDeNegocioException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;


public class ContaService {

    private ConnectionFactory connection;

    public ContaService(){
        this.connection = new ConnectionFactory();
    }



    public Set<Conta> listarContasAbertas() {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).listar();
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        Connection conn = connection.recuperarConexao();
        return new ContaDAO(conn).consultarSaldo(numeroDaConta);
        
    }

    public void abrir(DadosAberturaConta dadosDaConta) {
      
        Connection conn = connection.recuperarConexao();     
        new ContaDAO(conn).salvar(dadosDaConta);  
       
    }

    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
       Connection conn = connection.recuperarConexao();
       ContaDAO contaDAO = new ContaDAO(conn);

        try {
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RegraDeNegocioException("Valor do saque deve ser superior a zero!");
            }

            contaDAO.realizarSaque(numeroDaConta, valor);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
    Connection conn = connection.recuperarConexao();
    ContaDAO contaDAO = new ContaDAO(conn);

    try {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraDeNegocioException("Valor do depósito deve ser superior a zero!");
        }

        contaDAO.realizarDeposito(numeroDaConta, valor);
    } finally {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

    public void encerrar(Integer numeroDaConta) {
      Connection conn = connection.recuperarConexao();
      ContaDAO contaDAO = new ContaDAO(conn);

        try {
            contaDAO.encerrarConta(numeroDaConta);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // private Conta buscarContaPorNumero(Integer numero) {
    //     return contas
    //             .stream()
    //             .filter(c -> c.getNumero() == numero)
    //             .findFirst()
    //             .orElseThrow(() -> new RegraDeNegocioException("Não existe conta cadastrada com esse número!"));
    // }
}
