import { DomCreate } from "./DomCreate.js";

export class DomHtml {

    /**
     * 링크가 필요한 본문에 사용.
     * 정규표현식으로 본문 내 링크를 찾고 <a></a> 태그로 감싼 후 parentNode.innerHTML 로 본문 채우기.
     */
    static addHyperLink(node, nodeId, content) {
        if (node && content !== undefined) {
            const urlPattern = /(https?:\/\/[^\s]+)/g;

            const splits = content.split(urlPattern);
            
            const iterateSize = splits.length;

            let anchorIncrement = 0;
            let spanIncrement = 0;

            for (let i = 0; i < iterateSize; i++) {
                if (urlPattern.test(splits[i])) {
                    const anchor = DomCreate.anchor(
                        nodeId + "-anchor-" + anchorIncrement++
                        , "text-break"
                        , splits[i]
                        , splits[i]
                    )
                    anchor.target = "_blank";

                    node.appendChild(anchor);
                    continue;
                }

                const span = DomCreate.span(
                    nodeId + "-span-" + spanIncrement++
                    , null
                    , splits[i]
                );

                node.appendChild(span);
            }
        }
    }
}
