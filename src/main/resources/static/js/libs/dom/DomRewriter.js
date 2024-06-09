export class DocumentRewriter {
    static rewriteWith(data) {
        document.open();
        document.write(data);
        document.close();
    }
}