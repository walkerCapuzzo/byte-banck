package br.com.alura.bytebank.domain.conta;

import br.com.alura.bytebank.domain.cliente.Cliente;
import br.com.alura.bytebank.domain.cliente.DadosCadastroCliente;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;



public class ContaDAO {

    private Connection conn;

    ContaDAO(Connection connection) {
        this.conn = connection;
    }

    public void salvar(DadosAberturaConta dadosDaConta){
        var cliente = new Cliente(dadosDaConta.dadosCliente());
        var conta = new Conta(dadosDaConta.numero(), cliente);
        String sql = "INSERT INTO conta (numero, saldo, cliente_nome, cliente_cpf, cliente_email)" + "VALUES (?, ?, ?, ?, ?)";

        
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, conta.getNumero());
            preparedStatement.setBigDecimal(2, BigDecimal.ZERO);
            preparedStatement.setString(3, dadosDaConta.dadosCliente().nome());
            preparedStatement.setString(4, dadosDaConta.dadosCliente().cpf());
            preparedStatement.setString(5, dadosDaConta.dadosCliente().email());

            preparedStatement.execute();
            preparedStatement.close();
            conn.close();

        } catch (SQLException e ){
            throw new RuntimeException(e);
        }
    }

    public Set<Conta> listar() {
        PreparedStatement ps;
        ResultSet resultSet;

        Set<Conta> contas = new HashSet<>();

        String sql = "SELECT * FROM conta";

        try{
            ps = conn.prepareStatement(sql);
            resultSet = ps.executeQuery();

            while(resultSet.next()) {
                Integer numero = resultSet.getInt(1);
                String nome = resultSet.getString(3);
                String cpf = resultSet.getString(4);
                String email = resultSet.getString(5);

                DadosCadastroCliente dadosCadastroCliente = 
                    new DadosCadastroCliente(nome, cpf, email);
                Cliente cliente = new Cliente(dadosCadastroCliente);

                contas.add(new Conta(numero, cliente));
            };
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contas;
    }

    public BigDecimal consultarSaldo(Integer numeroDaConta) {
        PreparedStatement ps;
        ResultSet resultSet;
        BigDecimal saldo = null;
    
        String sql = "SELECT saldo FROM conta WHERE numero = ?";
    
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, numeroDaConta);
            resultSet = ps.executeQuery();
    
            if (resultSet.next()) {
                saldo = resultSet.getBigDecimal(1);
            } else {
                System.out.println("Conta não encontrada");
            }
            resultSet.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    
        return saldo;
    }

    public void realizarDeposito(Integer numeroDaConta, BigDecimal valor) {
        PreparedStatement ps = null;
    
        String sql = "UPDATE conta SET saldo = saldo + ? WHERE numero = ?";
    
        try {
            ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, valor);
            ps.setInt(2, numeroDaConta);
            int rowsAffected = ps.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Depósito realizado com sucesso.");
            } else {
                System.out.println("Conta não encontrada.");
            }
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

   
    public void realizarSaque(Integer numeroDaConta, BigDecimal valor) {
        PreparedStatement ps = null;
        ResultSet rs = null;
    
        String sqlSelect = "SELECT saldo FROM conta WHERE numero = ?";
        String sqlUpdate = "UPDATE conta SET saldo = saldo - ? WHERE numero = ?";
    
        try {
            // Verificar saldo disponível
            ps = conn.prepareStatement(sqlSelect);
            ps.setInt(1, numeroDaConta);
            rs = ps.executeQuery();
    
            if (rs.next()) {
                BigDecimal saldoAtual = rs.getBigDecimal("saldo");
    
                if (saldoAtual.compareTo(valor) >= 0) {
                    // Realizar saque
                    ps = conn.prepareStatement(sqlUpdate);
                    ps.setBigDecimal(1, valor);
                    ps.setInt(2, numeroDaConta);
                    int rowsAffected = ps.executeUpdate();
    
                    if (rowsAffected > 0) {
                        System.out.println("Saque realizado com sucesso.");
                    } else {
                        System.out.println("Conta não encontrada.");
                    }
                } else {
                    System.out.println("Saldo insuficiente para realizar o saque.");
                }
            } else {
                System.out.println("Conta não encontrada.");
            }
    
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            System.out.println("Valor inválido para saque: " + e.getMessage());
        } catch (ArithmeticException e) {
            System.out.println("Erro ao realizar o saque: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void encerrarConta (Integer numeroDaConta) {
        PreparedStatement ps = null;

        String sql = "DELETE FROM conta WHERE numero = ?";

        try{
            ps = conn.prepareStatement(sql);
            ps.setInt(1, numeroDaConta);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Conta encerrada com sucesso.");
            } else {
                System.out.println("Conta não encontrada.");
            }
            ps.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }
    
}
