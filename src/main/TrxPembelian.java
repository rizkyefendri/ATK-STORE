/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Taufik (fb.com/kang3s);
 */
public class TrxPembelian extends javax.swing.JFrame {

    /**
     * Creates new form TrxPenjualan
     */
    Connection conn;
    Statement stat;
    private DefaultTableModel tabmode;    
    public TrxPembelian() {
        initComponents();
        Koneksi DB = new Koneksi();
        DB.config();
        conn = DB.conn;
        stat = DB.stat;
        fullScreen(bg);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        setLayout();
    }

    public TrxPembelian(String petugas) {
        initComponents();
        Koneksi DB = new Koneksi();
        DB.config();
        conn = DB.conn;
        stat = DB.stat;
        fullScreen(bg);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        tfpetugas.setText(petugas);
        setLayout();
    }
    
    private void setLayout() {
        this.setBackground(new Color(0, 0, 0, 0));
        this.setIconImage(new ImageIcon(getClass().getResource("/img/storeset.png")).getImage());
        aktif();
        setDataTrx();
        tfkdsup.setText("SUP00");
        setFieldSup();
        tfisi.hide();
        tfttotaltemp.hide();
        cstn.setSelected(true);
    }

    private void aktif() {
        //tfkdbrg.requestFocus();
        //jtgl.setEditor(new JSpinner.DateEditor(jtgl,"dd/MM/yyyy"));
        Object[] Baris = {"NO", "KODE BARANG", "NAMA BARANG", "HARGA JUAL", "QTY", "JENIS", "TOTAL"};
        tabmode = new DefaultTableModel(null, Baris);
        tPembelian.setModel(tabmode);
        //Layouting JTable
        tPembelian.getTableHeader().setResizingAllowed(false);
        tPembelian.getTableHeader().setReorderingAllowed(false);
        tPembelian.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tPembelian.getTableHeader().setForeground(new Color(25, 181, 254));
        ((DefaultTableCellRenderer) tPembelian.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        ((DefaultTableCellRenderer) tPembelian.getTableHeader().getDefaultRenderer()).setVerticalAlignment(SwingConstants.TOP);
        DefaultTableCellRenderer crender = new DefaultTableCellRenderer();
        crender.setHorizontalAlignment(JLabel.CENTER);
        for (int columnIndex = 0; columnIndex < tPembelian.getColumnCount(); columnIndex++) {
            tPembelian.getColumnModel().getColumn(columnIndex).setCellRenderer(crender);
        }
        tPembelian.setRowHeight(25);
        tPembelian.getColumnModel().getColumn(0).setPreferredWidth(35);
        tPembelian.getColumnModel().getColumn(1).setPreferredWidth(130);
        tPembelian.getColumnModel().getColumn(2).setPreferredWidth(230);
        tPembelian.getColumnModel().getColumn(3).setPreferredWidth(150);
        tPembelian.getColumnModel().getColumn(4).setPreferredWidth(100);
        tPembelian.getColumnModel().getColumn(5).setPreferredWidth(150);
        tPembelian.getColumnModel().getColumn(6).setPreferredWidth(150);
    }
    
    private void kosong(){
        suppfieldKosong();
        brgfieldKosong();
        tfkdsup.setText("SUP00");
        tfkdbrg.setText("");
        tftunai.setText("");
        tfkembali.setText("");
        tfttotal.setText("");
       }
    
    private void suppfieldKosong() {
        tfnmsup.setText("");
        notlp.setText("");
        tfalmt.setText("");
    }

    private void brgfieldKosong() {
        tfnmbrg.setText("");
        tfhbstn.setText("");
        tfhbpcs.setText("");
        tfstn.setText("");
        tfstok.setText("");
        tfisi.setText("");
        tfqty.setText("");
        tfrealqty.setText("");
        cstn.setSelected(true);
        type.setText("");
        tftotal.setText("");
    }
    
    private void autonumber() {
        try {
            String sql = "SELECT id_notapembelian FROM  notapembelian order by id_notapembelian asc";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            tfidnota.setText("TRXB0001");
            while (rs.next()) {
                String id_nota = rs.getString("id_notapembelian").substring(4);
                int AN = Integer.parseInt(id_nota) + 1;
                String Nol = "";
                if (AN < 10) {
                    Nol = "000";
                } else if (AN < 100) {
                    Nol = "00";
                } else if (AN < 1000) {
                    Nol = "0";
                } else if (AN < 10000) {
                    Nol = "";
                }
                tfidnota.setText("TRXB" + Nol + AN);
            }
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Auto Number Gagal" + e);
        }
    }
    
    private void setDataTrx() {
        autonumber();
        getDate();
    }
    
    private void getDate() {
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = fm.format(date);
        txtDate.setText(strDate);
    }

    private void addItem() {
        try {
            int harga = 0;
            if (cstn.isSelected()) {
                harga = Integer.valueOf(tfhbstn.getText());
            } else if (!cstn.isSelected()) {
                harga = Integer.valueOf(tfhbpcs.getText());
            }
            String kode = tfkdbrg.getText();
            String nama = tfnmbrg.getText();
            String qty = tfrealqty.getText();
            String jenis = type.getText();
            String total = tftotal.getText();
            if (!"".equals(kode) && !"".equals(nama) && !"".equals(qty) && !"".equals(jenis) && !"".equals(total)) {
                int no;
                for (no = 1; no <= tPembelian.getRowCount();) {
                    no++;
                }
                tabmode.addRow(new Object[]{no, kode, nama, harga, qty, jenis, total});
                tPembelian.setModel(tabmode);
                hitung();
                brgfieldKosong();
                tfkdbrg.setText("");
                tfkdbrg.requestFocus();
            } else {
                JOptionPane.showMessageDialog(null, "Ada Inputan Kosong!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }
    }
    
    public void hitung() {
        int total = 0;
        for (int i = 0; i < tPembelian.getRowCount(); i++) {
            int amount = Integer.valueOf(tPembelian.getValueAt(i, 6).toString());
            total += amount;
        }
        tfttotaltemp.setText(String.valueOf(total));
        double ttl = Double.valueOf(total);
        rpCurr(ttl, tfttotal);
    }

    private void setFieldSup() {
        String kode = tfkdsup.getText();
         if(kode.length()!=0){
             String sql = "select * from supplier where kode_supplier=?";
            try{
                PreparedStatement pst=conn.prepareStatement(sql);
                pst.setString(1, kode);
                ResultSet res = pst.executeQuery();
                if(res.next()){
                    tfnmsup.setText(res.getString("nama_supplier"));
                    notlp.setText(res.getString("notelp"));
                    tfalmt.setText(res.getString("alamat"));
                    tfkdbrg.requestFocus();
                }else{
                    suppfieldKosong();
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(rootPane, e);
            }
         }else if(kode.length()==0){
            suppfieldKosong();   
 }
    }

    private void save() throws ParseException {
        try {
            Date tglx = new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(txtDate.getText()));
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
            String fd = fm.format(tglx);
            String sql = "insert into notapembelian values (?,?,?,?)";
            String zsql = "insert into isi_notapembelian values (?,?,?,?,?,?)";
            try {
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, tfidnota.getText());
                pst.setString(2, fd);
                pst.setString(3, tfkdsup.getText());
                pst.setString(4, tfpetugas.getText());
                pst.executeUpdate();

                int t = tPembelian.getRowCount();
                for (int i = 0; i < t; i++) {
                    String xkd = tPembelian.getValueAt(i, 1).toString();
                    String xhg = tPembelian.getValueAt(i, 3).toString();
                    String xqty = tPembelian.getValueAt(i, 4).toString();
                    String xjenis = tPembelian.getValueAt(i, 5).toString();
                    int oqty = Integer.valueOf(xqty);
                    int isi = 0;
                    int stokIn = 0;
                    try {
                        String sqlxx = "select * from satuan where nama_satuan='" + xjenis + "'";
                        Statement stm = conn.createStatement();
                        ResultSet res = stm.executeQuery(sqlxx);
                        if (res.next()) {
                            isi = Integer.valueOf(res.getString("isi"));
                        }
                        stokIn = oqty * isi;
                    } catch (NumberFormatException | SQLException e) {
                        e.getMessage();
                    }
                    String stok_masuk = String.valueOf(stokIn);

                    PreparedStatement stat2 = conn.prepareStatement(zsql);

                    stat2.setString(1, tfidnota.getText());
                    stat2.setString(2, xkd);
                    stat2.setString(3, xhg);
                    stat2.setString(4, xqty);
                    stat2.setString(5, xjenis);
                    stat2.setString(6, stok_masuk);
                    stat2.executeUpdate();
                }
                Alert al = new Alert("Saved Successfully!", "/img/ok.png", new Color(33,191,115));
                al.setVisible(true);
                al.fadeOut(al);
                al.setVisible(false);
                this.setFocusableWindowState(false);
//                db.setFocusableWindowState(false);
//                db.toBack();
                cetak();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "data gagal disimpan" + e);
            }
//            updateStok();
            kosong();
            aktif();
            autonumber();
            setFieldSup();
        } catch (HeadlessException e) {
            e.printStackTrace();
        }
    }
    
    public void cetak(){
        try {
           String path="./src/main/StrukPembelian.jasper"; // letakpenyimpanan report
           HashMap parameter = new HashMap();
           parameter.put("id_nota",tfidnota.getText());
           parameter.put("tunai",tftunai.getText());
           parameter.put("kembali",tfkembali.getText());
           JasperPrint print = JasperFillManager.fillReport(path,parameter,conn);
           JasperViewer jv = new JasperViewer(print, false);
           jv.setTitle("Struk");
           jv.setSize(350,570);
           final Dimension dms = Toolkit.getDefaultToolkit().getScreenSize();
           final int x = (dms.width - jv.getWidth())/2;
           final int y = (dms.height - jv.getHeight())/2;
           jv.setLocation(x, y);
           jv.setZoomRatio(new Float(1.20));
           jv.setResizable(false);
           jv.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent evt) {
//                    db.setFocusableWindowState(true);
//                    db.toBack();
                    setFocusableWindowState(true);
                    toFront();                   
                    tfkdsup.requestFocus();
                }
            });
           jv.show();
        }catch (JRException ex) {
            JOptionPane.showMessageDialog(null,"DokumenTidak Ada "+ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        header = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        tfisi = new javax.swing.JTextField();
        tfttotaltemp = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        tgl = new javax.swing.JLabel();
        kasir = new javax.swing.JLabel();
        tfidnota = new javax.swing.JTextField();
        txtDate = new javax.swing.JTextField();
        tfpetugas = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        tfkdsup = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        tfalmt = new javax.swing.JTextField();
        notlp = new javax.swing.JTextField();
        jPanel22 = new javax.swing.JPanel();
        tfnmsup = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        tfkdbrg = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        tfnmbrg = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        tftotal = new javax.swing.JTextField();
        tfqty = new javax.swing.JTextField();
        tfhbstn = new javax.swing.JTextField();
        tfhbpcs = new javax.swing.JTextField();
        jPanel19 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        tfstn = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        tfstok = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cstn = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        tfrealqty = new javax.swing.JTextField();
        type = new javax.swing.JTextField();
        jPanel21 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JPanel();
        lblAdd = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tPembelian = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tfttotal = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        tftunai = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        tfkembali = new javax.swing.JTextField();
        btnSimpan = new javax.swing.JPanel();
        lblSimpan = new javax.swing.JLabel();
        btnBatal = new javax.swing.JPanel();
        lblBatal = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        bg.setBackground(new java.awt.Color(255, 255, 255));

        header.setBackground(new java.awt.Color(235, 248, 252));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 17)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(25, 181, 254));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(". : Transaksi Pembelian : .");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/closeb.png"))); // NOI18N
        jLabel11.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel11MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tfisi, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1016, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(tfttotaltemp, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tfisi)
            .addGroup(headerLayout.createSequentialGroup()
                .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(tfttotaltemp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 1, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jPanel2.setBackground(new java.awt.Color(248, 248, 248));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(188, 188, 188));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Data Transaksi");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        id.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        id.setForeground(new java.awt.Color(188, 188, 188));
        id.setText("ID Trx");

        tgl.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        tgl.setForeground(new java.awt.Color(188, 188, 188));
        tgl.setText("Tanggal");

        kasir.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        kasir.setForeground(new java.awt.Color(188, 188, 188));
        kasir.setText("Petugas");

        tfidnota.setEditable(false);
        tfidnota.setBackground(new java.awt.Color(248, 248, 248));
        tfidnota.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfidnota.setForeground(new java.awt.Color(25, 181, 254));
        tfidnota.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfidnota.setBorder(null);

        txtDate.setEditable(false);
        txtDate.setBackground(new java.awt.Color(248, 248, 248));
        txtDate.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtDate.setForeground(new java.awt.Color(25, 181, 254));
        txtDate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtDate.setBorder(null);

        tfpetugas.setEditable(false);
        tfpetugas.setBackground(new java.awt.Color(248, 248, 248));
        tfpetugas.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfpetugas.setForeground(new java.awt.Color(25, 181, 254));
        tfpetugas.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfpetugas.setBorder(null);

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(188, 188, 188));
        jLabel10.setText(":");

        jLabel19.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(188, 188, 188));
        jLabel19.setText(":");

        jLabel22.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(188, 188, 188));
        jLabel22.setText(":");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tgl, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .addComponent(id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kasir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 5, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate)
                    .addComponent(tfidnota)
                    .addComponent(tfpetugas))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tfidnota, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tgl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfpetugas, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(kasir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jPanel4.setBackground(new java.awt.Color(248, 248, 248));

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(188, 188, 188));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Data Supplier");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        tfkdsup.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfkdsup.setForeground(new java.awt.Color(25, 181, 254));
        tfkdsup.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfkdsup.setBorder(null);
        tfkdsup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfkdsupKeyReleased(evt);
            }
        });

        jPanel17.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/alamat.png"))); // NOI18N

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/notlp.png"))); // NOI18N

        tfalmt.setEditable(false);
        tfalmt.setBackground(new java.awt.Color(255, 255, 255));
        tfalmt.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfalmt.setForeground(new java.awt.Color(188, 188, 188));
        tfalmt.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        tfalmt.setBorder(null);

        notlp.setEditable(false);
        notlp.setBackground(new java.awt.Color(255, 255, 255));
        notlp.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        notlp.setForeground(new java.awt.Color(188, 188, 188));
        notlp.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        notlp.setBorder(null);

        jPanel22.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        tfnmsup.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfnmsup.setForeground(new java.awt.Color(25, 181, 254));
        tfnmsup.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfnmsup.setBorder(null);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfkdsup)
                    .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfnmsup)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(notlp)
                            .addComponent(tfalmt))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tfkdsup, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(tfnmsup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfalmt, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(notlp, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jPanel6.setBackground(new java.awt.Color(248, 248, 248));

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(188, 188, 188));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Data Barang");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
        );

        tfkdbrg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfkdbrg.setForeground(new java.awt.Color(25, 181, 254));
        tfkdbrg.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfkdbrg.setBorder(null);
        tfkdbrg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfkdbrgKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfkdbrgKeyReleased(evt);
            }
        });

        jPanel18.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        tfnmbrg.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfnmbrg.setForeground(new java.awt.Color(25, 181, 254));
        tfnmbrg.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfnmbrg.setBorder(null);

        jLabel16.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel16.setText("Qty       :");

        jLabel17.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel17.setText("Total    :");

        jLabel20.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(188, 188, 188));
        jLabel20.setText("Harga Satuan        :");

        jLabel21.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(188, 188, 188));
        jLabel21.setText("Harga Pcs              :");

        tftotal.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tftotal.setForeground(new java.awt.Color(25, 181, 254));

        tfqty.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfqty.setForeground(new java.awt.Color(188, 188, 188));
        tfqty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tfqtyKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tfqtyKeyReleased(evt);
            }
        });

        tfhbstn.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfhbstn.setForeground(new java.awt.Color(188, 188, 188));
        tfhbstn.setBorder(null);

        tfhbpcs.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfhbpcs.setForeground(new java.awt.Color(188, 188, 188));
        tfhbpcs.setBorder(null);

        jPanel19.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(188, 188, 188));
        jLabel6.setText("Satuan                    :");

        tfstn.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfstn.setForeground(new java.awt.Color(188, 188, 188));
        tfstn.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        tfstn.setBorder(null);
        tfstn.setMaximumSize(new java.awt.Dimension(1000, 25));
        tfstn.setMinimumSize(new java.awt.Dimension(60, 19));

        jLabel15.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(188, 188, 188));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Stok :");
        jLabel15.setMaximumSize(new java.awt.Dimension(20, 16));

        tfstok.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfstok.setForeground(new java.awt.Color(25, 181, 254));
        tfstok.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfstok.setBorder(null);
        tfstok.setMinimumSize(new java.awt.Dimension(35, 19));

        jLabel18.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(188, 188, 188));
        jLabel18.setText("Jenis Pembelian  :");

        cstn.setBackground(new java.awt.Color(255, 255, 255));
        cstn.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        cstn.setForeground(new java.awt.Color(188, 188, 188));
        cstn.setText("Satuan");
        cstn.setIconTextGap(8);
        cstn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cstnActionPerformed(evt);
            }
        });

        jPanel10.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        tfrealqty.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        tfrealqty.setForeground(new java.awt.Color(25, 181, 254));
        tfrealqty.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfrealqty.setBorder(null);

        type.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        type.setForeground(new java.awt.Color(25, 181, 254));
        type.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        type.setBorder(null);

        jPanel21.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfkdbrg)
                    .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfnmbrg)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tftotal))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfhbstn, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(tfhbpcs)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfqty, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfrealqty, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(type))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cstn, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(tfstn, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfstok, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tfkdbrg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfnmbrg, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfhbstn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfhbpcs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfstn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfstok, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cstn, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tfqty)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfrealqty)
                    .addComponent(type))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tftotal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        btnAdd.setBackground(new java.awt.Color(33, 191, 115));

        lblAdd.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblAdd.setForeground(new java.awt.Color(255, 255, 255));
        lblAdd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/add2.png"))); // NOI18N
        lblAdd.setText("Tambah");
        lblAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblAdd.setIconTextGap(8);
        lblAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAddMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAddMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAddMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblAddMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblAddMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout btnAddLayout = new javax.swing.GroupLayout(btnAdd);
        btnAdd.setLayout(btnAddLayout);
        btnAddLayout.setHorizontalGroup(
            btnAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        btnAddLayout.setVerticalGroup(
            btnAddLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblAdd, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
        );

        jPanel8.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        tPembelian.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        tPembelian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tPembelian.setGridColor(new java.awt.Color(255, 255, 255));
        tPembelian.setRowHeight(25);
        tPembelian.setSelectionBackground(new java.awt.Color(25, 181, 254));
        jScrollPane1.setViewportView(tPembelian);

        jPanel8.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)));

        jPanel16.setBackground(new java.awt.Color(235, 248, 252));

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(25, 181, 254));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Total Belanja");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
        );

        tfttotal.setEditable(false);
        tfttotal.setBackground(new java.awt.Color(255, 255, 255));
        tfttotal.setFont(new java.awt.Font("SansSerif", 1, 42)); // NOI18N
        tfttotal.setForeground(new java.awt.Color(25, 181, 254));
        tfttotal.setBorder(null);

        jLabel23.setFont(new java.awt.Font("SansSerif", 1, 42)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(25, 181, 254));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Rp.");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfttotal, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfttotal)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Tunai ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(25, 181, 254))); // NOI18N

        jLabel24.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(25, 181, 254));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Rp.");
        jLabel24.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        tftunai.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        tftunai.setForeground(new java.awt.Color(25, 181, 254));
        tftunai.setBorder(null);
        tftunai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tftunaiKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tftunaiKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tftunai)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tftunai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(218, 218, 218)), " Kembali ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 12), new java.awt.Color(33, 191, 115))); // NOI18N

        jLabel25.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(33, 191, 115));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("Rp.");
        jLabel25.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        tfkembali.setEditable(false);
        tfkembali.setBackground(new java.awt.Color(255, 255, 255));
        tfkembali.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        tfkembali.setForeground(new java.awt.Color(33, 191, 115));
        tfkembali.setBorder(null);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tfkembali, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(tfkembali)
        );

        btnSimpan.setBackground(new java.awt.Color(25, 181, 254));

        lblSimpan.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblSimpan.setForeground(new java.awt.Color(255, 255, 255));
        lblSimpan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/save2.png"))); // NOI18N
        lblSimpan.setText("Simpan");
        lblSimpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSimpan.setIconTextGap(15);
        lblSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSimpanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblSimpanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblSimpanMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblSimpanMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblSimpanMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout btnSimpanLayout = new javax.swing.GroupLayout(btnSimpan);
        btnSimpan.setLayout(btnSimpanLayout);
        btnSimpanLayout.setHorizontalGroup(
            btnSimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSimpan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
        );
        btnSimpanLayout.setVerticalGroup(
            btnSimpanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblSimpan, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
        );

        btnBatal.setBackground(new java.awt.Color(255, 255, 255));
        btnBatal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(188, 188, 188), 2));

        lblBatal.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lblBatal.setForeground(new java.awt.Color(188, 188, 188));
        lblBatal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cancel2.png"))); // NOI18N
        lblBatal.setText("Batal");
        lblBatal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblBatal.setIconTextGap(11);
        lblBatal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblBatalMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout btnBatalLayout = new javax.swing.GroupLayout(btnBatal);
        btnBatal.setLayout(btnBatalLayout);
        btnBatalLayout.setHorizontalGroup(
            btnBatalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        btnBatalLayout.setVerticalGroup(
            btnBatalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel20.setBackground(new java.awt.Color(218, 218, 218));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSimpan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 992, Short.MAX_VALUE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bgLayout.createSequentialGroup()
                .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bgLayout.createSequentialGroup()
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSimpan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBatal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bgLayout.createSequentialGroup()
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(bg, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel11MouseClicked
        fadeOut(this);
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel11MouseClicked

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        fadeIn(this);
        tfkdsup.requestFocusInWindow();        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened

    private void tfkdsupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfkdsupKeyReleased
        setFieldSup();       // TODO add your handling code here:
    }//GEN-LAST:event_tfkdsupKeyReleased

    private void tfkdbrgKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfkdbrgKeyReleased
        String kode = tfkdbrg.getText();
        if (kode.length() != 0) {
            try {
                String sql = "select barang.kode_barang, barang.nama_barang, merk.nama_merk, satuan.nama_satuan, satuan.isi, barang.stok, barang.hbsatuan, barang.hbpcs, barang.hjsatuan, barang.hjpcs  from barang JOIN merk ON barang.kode_merk = merk.kode_merk JOIN satuan ON satuan.kode_satuan = barang.kode_satuan where kode_barang=?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, kode);
                ResultSet res = pst.executeQuery();
                if (res.next()) {
                    tfnmbrg.setText(res.getString("nama_barang") + " " + res.getString("nama_merk"));
                    tfhbstn.setText(res.getString("hjsatuan"));
                    tfhbpcs.setText(res.getString("hjpcs"));
                    tfstn.setText(res.getString("nama_satuan"));
                    tfstok.setText(res.getString("stok"));
                    tfisi.setText(res.getString("isi"));
                    tfqty.requestFocus();
                } else {
                    brgfieldKosong();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(rootPane, "Error!\n"+e);
            }
        } else if (kode.length() == 0) {
            brgfieldKosong();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_tfkdbrgKeyReleased

    private void tfqtyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfqtyKeyReleased
        try {
            int hj;
            String aqty = tfqty.getText();
            String satuan = tfstn.getText();
            String stn;
            if (aqty.length() != 0) {
                int qty = Integer.valueOf(aqty);
                if (!cstn.isSelected()) {
                    stn = "";
                    type.setText(stn);
                    tfrealqty.setText(aqty);
                    hj = Integer.valueOf(tfhbpcs.getText());
                    tftotal.setText(String.valueOf(qty * hj));
                } else if (cstn.isSelected()) {
                    stn = satuan;
                    type.setText(stn);
                    tfrealqty.setText(aqty);
                    hj = Integer.valueOf(tfhbstn.getText());
                    tftotal.setText(String.valueOf(qty * hj));
                }
            } else if (aqty.length() == 0) {
                tfrealqty.setText("");
                type.setText("");
                tftotal.setText("");
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(rootPane, e);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_tfqtyKeyReleased

    private void tfqtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfqtyKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addItem();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_tfqtyKeyPressed

    private void cstnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cstnActionPerformed
        try {
            int hjstn = Integer.valueOf(tfhbstn.getText());
            int hj = Integer.valueOf(tfhbpcs.getText());
            int qty = Integer.valueOf(tfqty.getText());
            int isi = Integer.valueOf(tfisi.getText());
            int stok = Integer.valueOf(tfstok.getText());
            String stn = tfstn.getText();
            int realqty = qty * isi;
            if (cstn.isSelected()) {
                if (realqty <= stok) {
                    tfrealqty.setText(String.valueOf(qty));
                    type.setText(stn);
                    tftotal.setText(String.valueOf(hjstn * qty));
                } else {
                    JOptionPane.showMessageDialog(null, "Stok Tidak Mencukupi!");
                    cstn.setSelected(false);
                }
            } else if (!cstn.isSelected()) {
                tfrealqty.setText(String.valueOf(qty));
                type.setText("PCS");
                tftotal.setText(String.valueOf(qty * hj));
            }
        } catch (HeadlessException | NumberFormatException e) {
            JOptionPane.showMessageDialog(rootPane, "Error!\n"+e);
        }
        tfqty.requestFocus();        // TODO add your handling code here:
    }//GEN-LAST:event_cstnActionPerformed

    private void tftunaiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tftunaiKeyReleased
        try {
            String a = tftunai.getText();
            if (a.length() != 0) {
                int total = Integer.valueOf(tfttotaltemp.getText());
                int tunai = Integer.valueOf(a);
                double jumlah = Double.valueOf(tunai - total);                
                rpCurr(jumlah, tfkembali);
            } else if (a.length() == 0) {
                tfkembali.setText("");
            }
        } catch (NumberFormatException e) {
            e.getMessage();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_tftunaiKeyReleased

    private void tfkdbrgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfkdbrgKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
            tftunai.requestFocus();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_tfkdbrgKeyPressed

    private void tftunaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tftunaiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                double jml = Double.valueOf(tftunai.getText());
                rpCurr(jml, tftunai);
                save();                
            } catch (ParseException ex) {
                Logger.getLogger(TrxPembelian.class.getName()).log(Level.SEVERE, null, ex);
            }
        }// TODO add your handling code here:
    }//GEN-LAST:event_tftunaiKeyPressed

    private void lblAddMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMouseEntered
setItemColor(btnAdd, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMouseEntered

    private void lblAddMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMouseExited
setItemColor(btnAdd, new Color(33,191,115));        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMouseExited

    private void lblAddMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMousePressed
setItemColor(btnAdd, new Color(14,176,85));        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMousePressed

    private void lblAddMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMouseReleased
setItemColor(btnAdd, new Color(89,214,116));        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMouseReleased

    private void lblAddMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAddMouseClicked
addItem();        // TODO add your handling code here:
    }//GEN-LAST:event_lblAddMouseClicked

    private void lblSimpanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpanMouseEntered
setItemColor(btnSimpan, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_lblSimpanMouseEntered

    private void lblSimpanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpanMouseExited
setItemColor(btnSimpan, new Color(25, 181, 254));        // TODO add your handling code here:
    }//GEN-LAST:event_lblSimpanMouseExited

    private void lblSimpanMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpanMousePressed
setItemColor(btnSimpan, new Color(6, 166, 224));        // TODO add your handling code here:
    }//GEN-LAST:event_lblSimpanMousePressed

    private void lblSimpanMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpanMouseReleased
setItemColor(btnSimpan, new Color(81, 204, 255));        // TODO add your handling code here:
    }//GEN-LAST:event_lblSimpanMouseReleased

    private void lblSimpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpanMouseClicked
        try {
            save();        // TODO add your handling code here:
        } catch (ParseException ex) {
            Logger.getLogger(TrxPembelian.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lblSimpanMouseClicked

    private void lblBatalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBatalMouseClicked
        kosong();
        aktif();
        autonumber();
        setFieldSup();
        tfkdsup.requestFocus();// TODO add your handling code here:
    }//GEN-LAST:event_lblBatalMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TrxPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TrxPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TrxPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TrxPembelian.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TrxPembelian().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JPanel btnAdd;
    private javax.swing.JPanel btnBatal;
    private javax.swing.JPanel btnSimpan;
    private javax.swing.JCheckBox cstn;
    private javax.swing.JPanel header;
    private javax.swing.JLabel id;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel kasir;
    private javax.swing.JLabel lblAdd;
    private javax.swing.JLabel lblBatal;
    private javax.swing.JLabel lblSimpan;
    private javax.swing.JTextField notlp;
    private javax.swing.JTable tPembelian;
    private javax.swing.JTextField tfalmt;
    private javax.swing.JTextField tfhbpcs;
    private javax.swing.JTextField tfhbstn;
    private javax.swing.JTextField tfidnota;
    private javax.swing.JTextField tfisi;
    private javax.swing.JTextField tfkdbrg;
    private javax.swing.JTextField tfkdsup;
    private javax.swing.JTextField tfkembali;
    private javax.swing.JTextField tfnmbrg;
    private javax.swing.JTextField tfnmsup;
    private javax.swing.JTextField tfpetugas;
    private javax.swing.JTextField tfqty;
    private javax.swing.JTextField tfrealqty;
    private javax.swing.JTextField tfstn;
    private javax.swing.JTextField tfstok;
    private javax.swing.JTextField tftotal;
    private javax.swing.JTextField tfttotal;
    private javax.swing.JTextField tfttotaltemp;
    private javax.swing.JTextField tftunai;
    private javax.swing.JLabel tgl;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField type;
    // End of variables declaration//GEN-END:variables
    
    private void fullScreen(JPanel panel) {
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        panel.setSize(sz.width, sz.height);
    }

    private void fadeIn(JFrame fm) {
        for (double i = 0.0; i <= 1.0; i = i + 0.1) {
            String val = i + "";
            float f = Float.valueOf(val);
            fm.setOpacity(f);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    private void fadeOut(JFrame fm) {
        for (double i = 1.0; i >= 0.0; i = i - 0.1) {
            String val = i + "";
            float f = Float.valueOf(val);
            fm.setOpacity(f);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.getMessage();
            }
        }
    }

    private void setItemColor(JPanel panel, Color c){
        panel.setBackground(c);
    }
    
    private void rpCurr(double harga, JTextField a) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setCurrencySymbol("");
        dfs.setMonetaryDecimalSeparator(',');
        dfs.setGroupingSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String b = String.valueOf(df.format(harga));
        StringBuilder sb = new StringBuilder(b);
        sb.replace(b.lastIndexOf(",00"), b.lastIndexOf(",00") + 3, ",-");
        b = sb.toString();
        a.setText(b);
    }


}
