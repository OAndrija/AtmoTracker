namespace Blockchain
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            textBox_port = new TextBox();
            btn_connect = new Button();
            btn_mine = new Button();
            btn_connect_to = new Button();
            textBox_connect_to = new TextBox();
            textBox_username = new TextBox();
            label1 = new Label();
            label2 = new Label();
            richTextBox_chain = new RichTextBox();
            richTextBox_blocks = new RichTextBox();
            label4 = new Label();
            label5 = new Label();
            SuspendLayout();
            // 
            // textBox_port
            // 
            textBox_port.Location = new Point(135, 91);
            textBox_port.Margin = new Padding(4, 5, 4, 5);
            textBox_port.Name = "textBox_port";
            textBox_port.Size = new Size(124, 31);
            textBox_port.TabIndex = 1;
            textBox_port.TextChanged += textBox_port_TextChanged;
            // 
            // btn_connect
            // 
            btn_connect.Enabled = false;
            btn_connect.Location = new Point(268, 46);
            btn_connect.Margin = new Padding(4, 5, 4, 5);
            btn_connect.Name = "btn_connect";
            btn_connect.Size = new Size(106, 36);
            btn_connect.TabIndex = 1;
            btn_connect.Text = "Connect";
            btn_connect.UseVisualStyleBackColor = true;
            btn_connect.Click += btn_connect_Click;
            // 
            // btn_mine
            // 
            btn_mine.Enabled = false;
            btn_mine.Location = new Point(381, 46);
            btn_mine.Margin = new Padding(4, 5, 4, 5);
            btn_mine.Name = "btn_mine";
            btn_mine.Size = new Size(106, 36);
            btn_mine.TabIndex = 2;
            btn_mine.Text = "Mine";
            btn_mine.UseVisualStyleBackColor = true;
            btn_mine.Click += btn_mine_Click;
            // 
            // btn_connect_to
            // 
            btn_connect_to.Enabled = false;
            btn_connect_to.Location = new Point(627, 46);
            btn_connect_to.Margin = new Padding(4, 5, 4, 5);
            btn_connect_to.Name = "btn_connect_to";
            btn_connect_to.Size = new Size(135, 36);
            btn_connect_to.TabIndex = 3;
            btn_connect_to.Text = "Connect port";
            btn_connect_to.UseVisualStyleBackColor = true;
            btn_connect_to.Click += btn_connect_to_Click;
            // 
            // textBox_connect_to
            // 
            textBox_connect_to.Enabled = false;
            textBox_connect_to.Location = new Point(540, 48);
            textBox_connect_to.Margin = new Padding(4, 5, 4, 5);
            textBox_connect_to.Name = "textBox_connect_to";
            textBox_connect_to.Size = new Size(79, 31);
            textBox_connect_to.TabIndex = 4;
            textBox_connect_to.TextChanged += textBox_connect_to_TextChanged;
            // 
            // textBox_username
            // 
            textBox_username.Location = new Point(135, 48);
            textBox_username.Margin = new Padding(4, 5, 4, 5);
            textBox_username.Name = "textBox_username";
            textBox_username.Size = new Size(124, 31);
            textBox_username.TabIndex = 0;
            textBox_username.TextChanged += textBox_username_TextChanged;
            // 
            // label1
            // 
            label1.AutoSize = true;
            label1.Location = new Point(15, 51);
            label1.Margin = new Padding(4, 0, 4, 0);
            label1.Name = "label1";
            label1.Size = new Size(63, 25);
            label1.TabIndex = 6;
            label1.Text = "Name:";
            // 
            // label2
            // 
            label2.AutoSize = true;
            label2.Location = new Point(15, 95);
            label2.Margin = new Padding(4, 0, 4, 0);
            label2.Name = "label2";
            label2.Size = new Size(48, 25);
            label2.TabIndex = 7;
            label2.Text = "Port:";
            // 
            // richTextBox_chain
            // 
            richTextBox_chain.ForeColor = Color.Green;
            richTextBox_chain.HideSelection = false;
            richTextBox_chain.Location = new Point(408, 172);
            richTextBox_chain.Margin = new Padding(4, 5, 4, 5);
            richTextBox_chain.Name = "richTextBox_chain";
            richTextBox_chain.ReadOnly = true;
            richTextBox_chain.Size = new Size(354, 507);
            richTextBox_chain.TabIndex = 8;
            richTextBox_chain.Text = "";
            // 
            // richTextBox_blocks
            // 
            richTextBox_blocks.HideSelection = false;
            richTextBox_blocks.Location = new Point(20, 176);
            richTextBox_blocks.Margin = new Padding(4, 5, 4, 5);
            richTextBox_blocks.Name = "richTextBox_blocks";
            richTextBox_blocks.ReadOnly = true;
            richTextBox_blocks.Size = new Size(354, 507);
            richTextBox_blocks.TabIndex = 9;
            richTextBox_blocks.Text = "";
            // 
            // label4
            // 
            label4.AutoSize = true;
            label4.Location = new Point(15, 142);
            label4.Margin = new Padding(4, 0, 4, 0);
            label4.Name = "label4";
            label4.Size = new Size(67, 25);
            label4.TabIndex = 11;
            label4.Text = "Mining";
            // 
            // label5
            // 
            label5.AutoSize = true;
            label5.Location = new Point(408, 142);
            label5.Margin = new Padding(4, 0, 4, 0);
            label5.Name = "label5";
            label5.Size = new Size(154, 25);
            label5.TabIndex = 12;
            label5.Text = "Blockchain Ledger";
            // 
            // Form1
            // 
            AutoScaleDimensions = new SizeF(10F, 25F);
            AutoScaleMode = AutoScaleMode.Font;
            ClientSize = new Size(788, 702);
            Controls.Add(label5);
            Controls.Add(label4);
            Controls.Add(richTextBox_blocks);
            Controls.Add(richTextBox_chain);
            Controls.Add(label2);
            Controls.Add(label1);
            Controls.Add(textBox_username);
            Controls.Add(textBox_connect_to);
            Controls.Add(btn_connect_to);
            Controls.Add(btn_mine);
            Controls.Add(btn_connect);
            Controls.Add(textBox_port);
            Margin = new Padding(4, 5, 4, 5);
            Name = "Form1";
            Text = "Blockchain";
            Load += Form1_Load;
            ResumeLayout(false);
            PerformLayout();
        }

        #endregion

        private System.Windows.Forms.TextBox textBox_port;
        private System.Windows.Forms.Button btn_connect;
        private System.Windows.Forms.Button btn_mine;
        private System.Windows.Forms.Button btn_connect_to;
        private System.Windows.Forms.TextBox textBox_connect_to;
        private System.Windows.Forms.TextBox textBox_username;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.RichTextBox richTextBox_chain;
        private System.Windows.Forms.RichTextBox richTextBox_blocks;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label5;
    }
}

