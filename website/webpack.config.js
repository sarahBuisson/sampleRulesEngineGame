const path = require('path');
const HtmlWebPackPlugin = require("html-webpack-plugin");
module.exports = {

    entry: {
        web: './src/index.js',
        html: "./src/index.html",
    },
    devServer: {
        contentBase: path.join(__dirname, 'dist'),
        port: 9600
        ,
        historyApiFallback: true
    },
    output: {
        filename: '[name].bundle.js',
        path: path.resolve(__dirname, 'dist')
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: {
                    loader: "babel-loader"
                }
            },
            {
                test: /\.html$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: "html-loader"
                    }
                ]
            }, {test: /\.css$/, loader: "style-loader!css-loader"}]

    },
    plugins: [
        new HtmlWebPackPlugin({
            template: "./src/index.html",
            filename: "./index.html",
            chunks: ['web'],

        })
    ]
};
